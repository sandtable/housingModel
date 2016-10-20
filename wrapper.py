#!/usr/bin/env python
from glob import glob
import json
import os
import sys
import shutil

import logging
logger = logging.getLogger(__name__)

import pandas as pd
import numpy as np

logger.setLevel(logging.INFO)

from simpy.shell_simulator import ShellSimulator

class WrappedModel(ShellSimulator):

    config_path = 'config.properties'
    output_path = '.'

    def __init__(self, config):
        super(WrappedModel, self).__init__(config)

        # change directory
        logger.info('change working directory: {}'.format(os.getcwd()))
        os.chdir(self.output_folder)

        shutil.copyfile('../../housingModel_ST.jar', 'housingModel_ST.jar')
        shutil.copyfile('../../modelsrc/data/AgeMarginalPDFstatic.csv', 'AgeMarginalPDFstatic.csv')
        shutil.copyfile('../../modelsrc/data/IncomeGivenAge.csv', 'IncomeGivenAge.csv')
        self.command = ['java', '-jar', '../../housingModel_ST.jar', str(self.control_parameters['seed']), self.config_path, self.output_path]

    def pre_processing(self):
        #config_prop = os.path.join(self.output_folder, 'config.properties')
        with open(self.config_path, 'w') as f_config:
            f_config.write('\n'.join(['{}={}'.format(k, v)
                           for k, v in self.model_parameters.items()]))

    def post_processing(self):
        self.output_file['output'] = self.get_output()
        self.output_file['coreIndicator'] = self.get_indicator('coreIndicator')

        self.close_output_file()

    def get_output(self):
        #df = pd.read_csv(os.path.join(self.output_folder, 'output-0.csv'))
        df = pd.read_csv('output-0.csv')
        # Remove unnecessary whitespace
        df.columns = [c.strip() for c in df.columns]
        return df

    def get_indicator(self, indicator):
        df = pd.DataFrame()
        for filename in sorted(glob(indicator+'-*.csv')):
            name = filename[len(indicator)+1:-4]
            df[name] = np.squeeze(pd.read_csv(filename, header=None).values)
        df['tick'] = range(len(df))
        df = df[np.hstack(('tick', df.columns[:-1]))]
        return df


if __name__ == "__main__":
    args = sys.argv
    if len(args) != 2:
        logger.error('Args: [config file]')
        sys.exit(1)
    config_file = args[1]

    config = json.load(open(config_file, 'r'))

    sim = WrappedModel(config)  # initialise
    sim()  # execute

    sys.exit(0)  # success
