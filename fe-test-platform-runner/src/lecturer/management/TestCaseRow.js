import React, {Component} from "react";
import backendUrl from "../../backendUrl";
import axios from "axios";
import {saveAs} from "file-saver";
import {CustomTableCell} from "../../styles/ProjectBoardStyles";
import IconButton from "@material-ui/core/IconButton/IconButton";
import UploadIcon from "@material-ui/icons/CloudUpload";
import DownloadIcon from "@material-ui/icons/CloudDownload";
import TableRow from "@material-ui/core/TableRow/TableRow";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/DeleteForever";

class TestCaseRow extends Component {

    handleDownloadInputFile = () => {
        this.downloadFile("input");
    };

    handleDownloadOutputFile = () => {
        this.downloadFile("output");
    };

    downloadFile = (fileName) => {
        console.log(backendUrl('/' + this.props.projectName + '/' + this.props.stageName + '/' + this.props.testCaseName + '/' + fileName));
        axios.get(backendUrl('/' + this.props.projectName + '/' + this.props.stageName + '/' + this.props.testCaseName + '/' + fileName), {responseType: "blob"})
            .then((response) => {
                console.log("Response", response);
                console.log("File name", fileName);
                saveAs(new Blob([response.data]), fileName);
            }).catch(function (error) {
            console.log(error);
            if (error.response) {
                console.log('Error', error.response.status);
            } else {
                console.log('Error', error.message);
            }
        });
    };

    render() {
        return (
            <TableRow>
                <CustomTableCell component="th" scope="row">
                    {this.props.testCaseName}
                    <IconButton aria-label="Edytuj">
                        <EditIcon/>
                    </IconButton>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row">
                    INPUT
                    <IconButton aria-label="Zmień">
                        <UploadIcon/>
                    </IconButton>
                    <IconButton aria-label="Pobierz" onClick={this.handleDownloadInputFile}>
                        <DownloadIcon/>
                    </IconButton>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row">
                    OUTPUT
                    <IconButton aria-label="Zmień">
                        <UploadIcon/>
                    </IconButton>
                    <IconButton aria-label="Pobierz" onClick={this.handleDownloadOutputFile}>
                        <DownloadIcon/>
                    </IconButton>
                </CustomTableCell>
                <CustomTableCell>
                    <CustomTableCell>
                        <IconButton aria-label="Usuń">
                            <DeleteIcon/>
                        </IconButton>
                    </CustomTableCell>
                </CustomTableCell>
                <CustomTableCell/>
            </TableRow>
        );
    }
}

export default (TestCaseRow);
