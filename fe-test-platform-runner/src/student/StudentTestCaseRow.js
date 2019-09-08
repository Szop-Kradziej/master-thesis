import React, {Component} from "react";
import {withStyles} from "@material-ui/core";
import green from "@material-ui/core/es/colors/green";
import red from "@material-ui/core/es/colors/red";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableCell from "@material-ui/core/TableCell/TableCell";
import * as Api from "../Api";
import DownloadItemComponent from "../utils/DownloadItemComponent";

class StudentTestCaseRow extends Component {

    handleDownloadLogsFile = () => {
        if (this.props.taskType === "stage") {
            Api.downloadStudentStageLogsFile(this.props.projectName, this.props.taskName, this.props.testCase.testCaseName)
        } else {
            Api.downloadStudentIntegrationLogsFile(this.props.projectName, this.props.taskName, this.props.testCase.testCaseName)
        }
    };

    handleDownloadInputFile = () => {
        this.downloadFile("input");
    };

    handleDownloadOutputFile = () => {
        this.downloadFile("output");
    };

    handleDownloadParametersFile = () => {
        this.downloadFile("parameters");
    };

    downloadFile = (fileType) => {
        if (this.props.taskType === "stage") {
            Api.downloadStageTestCaseFile(this.props.projectName, this.props.taskName, this.props.testCase.testCaseName, fileType)
        } else {
            Api.downloadIntegrationTestCaseFile(this.props.projectName, this.props.taskName, this.props.testCase.testCaseName, fileType)

        }
    };

    render() {
        return (
            <TableRow key="custom_key">
                <CustomTableCell component="th" scope="row" width="15%">
                    <p>{this.props.testCase.testCaseName}</p>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row" width="5%">
                    <p><font
                        color={this.props.testCase.status === 'SUCCESS' ? "green" : "red"}>{this.props.testCase.status}</font>
                    </p>
                </CustomTableCell>
                <CustomTableCell>
                    <p>{this.props.testCase.message ? this.props.testCase.message : "Brak"}</p>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row" width="5%">
                    <DownloadItemComponent header={this.props.testCase.logsFile === true ? "logs" : "Brak"}
                                           info="Pobierz logi"
                                           disabled={!this.props.testCase.logsFile === true}
                                           downloadActionHandler={this.handleDownloadLogsFile}/>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row" width="15%">
                    {this.props.testCase.parameters ?
                        <DownloadItemComponent header="parameters"
                                               info="Pobierz parametry"
                                               disabled={false}
                                               downloadActionHandler={this.handleDownloadParametersFile}/>
                        : "Brak"}
                </CustomTableCell>
                <CustomTableCell component="th" scope="row" width="8%">
                    <DownloadItemComponent header="input"
                                           info="Pobierz plik wejściowy"
                                           disabled={false}
                                           downloadActionHandler={this.handleDownloadInputFile}/>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row" width="8%">
                    <DownloadItemComponent header="output"
                                           info="Pobierz plik wyjściowy"
                                           disabled={false}
                                           downloadActionHandler={this.handleDownloadOutputFile}/>
                </CustomTableCell>
            </TableRow>
        );
    }
}

const CustomTableCell = withStyles(() => ({
    head: {
        color: "black",
        fontWeight: 700,
        fontSize: 12,
        margin: 0,
        padding: 0,
        border: 0
    },
    body: {
        color: "black",
        fontSize: 12,
        margin: 0,
        padding: 0,
        border: 0,
        height: 5
    }
}))(TableCell);

const styles = theme => ({
    root: {
        flexGrow: 1,
        width: 1700
    },
});

export default withStyles(styles)(StudentTestCaseRow);
