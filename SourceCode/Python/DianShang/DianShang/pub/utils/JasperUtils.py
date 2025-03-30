
import gc
import os
from pyreportjasper import PyReportJasper


# @ignore_warnings(Exception)
def processingReportJapser(jrxmlDir, jrxmlName, inputJsonFilePath, outputPdfFilePath):
    REPORTS_DIR = os.path.join(os.path.abspath(os.path.dirname(__file__)), 'reports')
    input_file = os.path.join(REPORTS_DIR, jrxmlName)
    # input_file = os.path.join(jrxmlDir, jrxmlName)
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
