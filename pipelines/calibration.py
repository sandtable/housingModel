# -*- coding: utf-8 -*-

import logging

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib
from luigi import Parameter

import sandluigi
from sandluigi.task import Task
from sandluigi.calibration import CalibrationResults, CalibrationData
from sandluigi.pipeline_results_target import PipelineResultsTargetFactory
# import cleanplot as cp

# Configure script logging
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
logger.propagate = False
logger.addHandler(logging.StreamHandler())

# Enable logs from sandluigi framework
sandluigi.configure_logging(logging.DEBUG)

# Pipeline version
__version__ = '0.0.6'

class RootTask(Task):
    """Process calibration and make a plot of sales prices.
    """
    calibration_id = Parameter()
    cp_context = Parameter('technical')
    fmt = Parameter('png')
    name = Parameter(None)
    label = Parameter(None)
    values = Parameter(None)

    def requires(self):
        """This task depends on a CalibrationResults task."""

        return {
            'results': CalibrationResults(
                calibration_id=self.calibration_id,
                **self.get_task_generic_args()),
            'data': CalibrationData(
                calibration_id=self.calibration_id,
                **self.get_task_generic_args()),
            }

    def run(self):
        """Parse configurations and create output data."""
        # cp.set_context(self.cp_context)
        data = self.read_data()
        parameters = self.read_sim_parameters()
        output = self.output()['plot']
        plot_result(data, parameters, self.values, output, self.label, self.fmt)
        
    def output(self):
        """Task output Target(s)."""
        name = self.name if self.name else self.calibration_id
        out = {
            'plot': PipelineResultsTargetFactory(
                results_name='{}.{}'.format(name, self.fmt),
                **self.get_target_generic_args()),
            # 'plot': LocalTarget('{}.{}'.format(name, self.fmt)),
        }
        return out

    def read_data(self):
        target_dicts = self.input()['data']
        data = {}
        for cid, targets in target_dicts.items():
            with targets['output'].open() as f_in:
                data[cid] = pd.read_csv(f_in)
        return data

    def read_sim_parameters(self):
        parameters_target = self.input()['results']
        with parameters_target.open('r') as f_parameters:
            parameters = pd.read_csv(f_parameters)
        parameters.set_index('configuration_id', inplace=True)
        return parameters

def plot_result(data, parameters, values, target, label, fmt):
    """Make the plot."""
    label = '{param} = {value}' if not label else label
    params = [c for c in parameters.columns
              if c not in ('configuration_id', 'front', 'dummy_metric')]
    fig = plt.figure(figsize=(15, 12))
    if values:
        order = []
        for value_dict in values:
            target_values = np.array([value_dict[p] for p in params])
            order.append(
                np.argmin(np.sum((parameters[params].values - target_values[None, :])**2, 1)))
        order = np.array(order)
    else:
        order = np.argsort(parameters[params[0]])
    cid_order = parameters.index[order]
    value_order = parameters[params].loc[cid_order].values
    n_config = len(order)
    n_col = int(np.floor(np.sqrt(n_config)))
    n_row = int(np.ceil(n_config / float(n_col)))
    for idx, (cid, value) in enumerate(zip(cid_order, value_order)):
        fig.add_subplot(n_row, n_col, idx+1)
        data_i = data[cid]
        center = data_i.AverageOfferPrice_mean / 1000.0
        lower = (data_i.AverageOfferPrice_mean - data_i.AverageOfferPrice_std) / 1000.0
        upper = (data_i.AverageOfferPrice_mean + data_i.AverageOfferPrice_std) / 1000.0
        plt.plot(center)
        plt.fill_between(data_i.index, lower, upper, alpha=0.3)
        label_i = '\n'.join([label.format(param=p, value=v) for p, v in zip(params, value)])
        plt.text(30, 225, label_i, fontsize=12)
        plt.xlabel('Model step')
        plt.ylabel(u'Average offer price (£1000s)')
        plt.xlim(0, 1500)
        plt.ylim(200, 600)
        ax = plt.gca()
        # cp.prep_plot(ax)
        for item in ([ax.title, ax.xaxis.label, ax.yaxis.label] +
                      ax.get_xticklabels() + ax.get_yticklabels()):
            item.set_fontsize(12)
    plt.tight_layout()
    with target.open('w') as f_out:
        fig.savefig(f_out, format=fmt)
