import React, {Component} from "react";
import {withStyles} from "@material-ui/core";
import green from "@material-ui/core/es/colors/green";
import red from "@material-ui/core/es/colors/red";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableCell from "@material-ui/core/TableCell/TableCell";
import IconButton from "@material-ui/core/IconButton/IconButton";
import DownloadIcon from "@material-ui/icons/CloudDownload";
import * as Api from "../Api";

class StudentTestCaseRow extends Component {

    handleDownloadLogsFile = () => {
        Api.downloadStudentLogsFile(this.props.projectName, this.props.stageName, this.props.testCase.testCaseName)
    };

    handleDownloadInputFile = () => {
        this.downloadFile("input");
    };

    handleDownloadOutputFile = () => {
        this.downloadFile("output");
    };

    downloadFile = (fileType) => {
        Api.downloadStageTestCaseFile(this.props.projectName, this.props.stageName, this.props.testCase.testCaseName, fileType)
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
                <CustomTableCell >
                    <p>{this.props.testCase.message ? this.props.testCase.message : "Brak"}</p>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row" width="5%">
                    {this.props.testCase.logsFile === true ? "Logi" : "Brak"}
                    <IconButton aria-label="Pobierz"
                                disabled={!this.props.testCase.logsFile === true}
                                onClick={this.handleDownloadLogsFile}>
                        <DownloadIcon/>
                    </IconButton>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row" width="15%">
                    {this.props.testCase.parameters === true ? "Parametry" : "Brak"}
                </CustomTableCell>
                <CustomTableCell component="th" scope="row" width="8%">
                    input
                    <IconButton aria-label="Pobierz"
                                disabled={!this.props.testCase.logsFile === true}
                                onClick={this.handleDownloadInputFile}>
                        <DownloadIcon/>
                    </IconButton>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row" width="8%">
                    output
                    <IconButton aria-label="Pobierz"
                                disabled={!this.props.testCase.logsFile === true}
                                onClick={this.handleDownloadOutputFile}>
                        <DownloadIcon/>
                    </IconButton>
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
