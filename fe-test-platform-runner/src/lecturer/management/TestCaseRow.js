import React, {Component} from "react";
import backendUrl from "../../backendUrl";
import axios from "axios";
import {saveAs} from "file-saver";
import {CustomTableCell} from "../../styles/ProjectBoardStyles";
import IconButton from "@material-ui/core/IconButton/IconButton";
import TableRow from "@material-ui/core/TableRow/TableRow";
import DeleteIcon from "@material-ui/icons/DeleteForever";
import EditItemComponent from "../../utils/EditItemComponent";
import UploadAndDownloadItemComponent from "../../utils/UploadAndDownloadItemComponent";

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

    handleUploadInputFile = () => {
        //TODO: do action
    };

    handleUploadOutputFile = () => {
        //TODO: do action
    };

    handleEditTestName = () => {
        //TODO: do action
    };

    render() {
        return (
            <TableRow>
                <CustomTableCell component="th" scope="row">
                    <EditItemComponent
                        header={this.props.testCaseName}
                        info="Edytuj nazwę testu"
                        editActionHandler={this.handleEditTestName}/>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row">
                    <UploadAndDownloadItemComponent
                        header="INPUT"
                        uploadInfo="Załaduj plik wejściowy"
                        uploadActionHandler={this.handleUploadInputFile}
                        downloadInfo="Pobierz plik wejściowy"
                        downloadActionHandler={this.handleDownloadInputFile}/>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row">
                    <UploadAndDownloadItemComponent
                        header="OUTPUT"
                        uploadInfo="Załaduj plik wyjściowy"
                        uploadActionHandler={this.handleUploadOutputFile}
                        downloadInfo="Pobierz plik wyjściowy"
                        downloadActionHandler={this.handleDownloadOutputFile}/>
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
