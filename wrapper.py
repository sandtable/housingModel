#!/usr/bin/env python

import sys
import json
from glob import glob

import logging
logger = logging.getLogger(__name__)

import pandas as pd
import numpy as np

logger.setLevel(logging.INFO)

from simpy.shell_simulator import ShellSimulator

class WrappedModel(ShellSimulator):

    def __init__(self, config):
        super(WrappedModel, self).__init__(config)
        # self.command = [
        #     'java',
        #     '-classpath',
        #     'modelsrc/:collectorsrc/:lib/bsh-2.0b4.jar:lib/commons-csv-1.1.jar:lib/commons-math3-3.3.jar:lib/itext-1.2.jar:lib/jcommon-1.0.21.jar:lib/jfreechart-1.0.17.jar:lib/jmf.jar:lib/mason.18.jar:lib/portfolio.jar:lib/vecmath.jar',
        #     'housing.Model']
        self.command = ['java', '-jar', 'model.jar']

    def pre_processing(self):
        with open('config.properties', 'w') as f_config:
            f_config.write('\n'.join(['{}={}'.format(k, v) for k, v in self.model_parameters.items()]))

    def post_processing(self):
        self.output_file['output'] = self.get_output()
        self.output_file['coreIndicator'] = self.get_indicator('coreIndicator')

    def get_output(self):
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
