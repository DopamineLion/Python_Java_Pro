# -*- coding: utf-8 -*-
import gc
import os
from pyreportjasper import PyReportJasper


def processingReportJapser(reportDir, jrxmlName, inputJsonFilePath, outputPdfFilePath):
    # REPORTS_DIR = os.path.join(os.path.abspath(os.path.dirname(__file__)), 'reports')
    input_file = os.path.join(reportDir, jrxmlName)
    pyreportjasper = PyReportJasper()
    conn = {
        'driver': 'json',
        'data_file': inputJsonFilePath,
        'json_query': ''
    }
    pyreportjasper.config(
        input_file,
        outputPdfFilePath,
        output_formats=["pdf"],
        db_connection=conn
    )
    pyreportjasper.process_report()
    del pyreportjasper
    gc.collect()
