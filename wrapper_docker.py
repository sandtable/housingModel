#!/usr/bin/env python
import json
from glob import glob
import os
import shutil
import sys

import logging
logger = logging.getLogger(__name__)

import pandas as pd
import numpy as np

from simpy.docker_simulator import DockerSimulator

logger.setLevel(logging.INFO)


class WrappedModel(DockerSimulator):

    def __init__(self, config):
        super(WrappedModel, self).__init__(config)

        self.config_file = os.path.join(self.output_folder,
                                        'config.properties')

        # container paths
        output_folder = '/mnt/data'
        config_file = os.path.join(output_folder, 'config.properties')

        # add volumes for input and output data
        volumes = ['-v', self.output_folder+':/mnt/data']
        image_tag = ['-t', self.docker_image]

        # docker CMD
        docker_cmd = ['java',
                      '-jar',
                      self.binaries['jar'],
                      str(self.control_parameters['seed']),
                      config_file,
                      output_folder]

        self.command.extend(volumes)
        self.command.extend(image_tag)
        self.command.extend(docker_cmd)

        logger.debug('Command: {}'.format(self.command))

    def pre_processing(self):
        with open(self.config_file, 'w') as f_config:
            f_config.write('\n'.join(['{}={}'.format(k, v)
                           for k, v in self.model_parameters.items()]))

    def post_processing(self):
        self.output_file['output'] = self.get_output()
        self.output_file['coreIndicator'] = self.get_indicator('coreIndicator')

        self.close_output_file()

    def get_output(self):
        df = pd.read_csv(os.path.join(self.output_folder, 'output-0.csv'))
        # Remove unnecessary whitespace
        df.columns = [c.strip() for c in df.columns]
        return df

    def get_indicator(self, indicator):
        df = pd.DataFrame()

        for filepath in sorted(glob(os.path.join(self.output_folder,
                                                 indicator+'-*.csv'))):
            filename = os.path.splitext(os.path.basename(filepath))[0]
            name = filename.split('-')[1]
            df[name] = np.squeeze(pd.read_csv(filepath, header=None).values)

        df['tick'] = range(len(df))
        df = df[np.hstack(('tick', df.columns[:-1]))]

        return df


if __name__ == '__main__':
    args = sys.argv
    if len(args) != 2:
        logger.error('Args: [config file]')
        sys.exit(1)
    config_file = args[1]

    config = json.load(open(config_file, 'r'))

    sim = WrappedModel(config)  # initialise
    sim()  # execute

    sys.exit(0)  # success
