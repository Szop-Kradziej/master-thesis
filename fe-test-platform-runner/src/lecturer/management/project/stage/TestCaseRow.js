import React, {Component} from "react";
import {CustomTableCell} from "../../../../styles/ProjectBoardStyles";
import TableRow from "@material-ui/core/TableRow/TableRow";
import EditItemComponent from "../../../../utils/EditItemComponent";
import UploadAndDownloadItemComponent from "../../../../utils/UploadAndDownloadItemComponent";
import * as Api from "../../../../Api";
import UploadTestCaseFileDialog from "./UploadTestCaseFileDialog";
import EditParametersDialog from "./dialog/EditParametersDialog";
import DeleteItemComponent from "../../../../utils/DeleteItemComponent";

class TestCaseRow extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isEditTestCaseParametersVisible: false,
            isAddInputTestCaseFileVisible: false,
            isAddOutputTestCaseFileVisible: false
        };
    }

    handleDownloadInputFile = () => {
        this.downloadFile("input");
    };

    handleDownloadOutputFile = () => {
        this.downloadFile("output");
    };

    downloadFile = (fileType) => {
        Api.downloadStageTestCaseFile(this.props.projectName, this.props.stageName, this.props.testCase.testCaseName, fileType)
    };

    handleOpenEditTestCaseParametersDialog = () => {
        this.setState({isEditTestCaseParametersVisible: true});
    };

    handleCloseEditTestCaseParametersDialog = () => {
        this.setState({isEditTestCaseParametersVisible: false});
    };

    handleOpenAddInputTestCaseFileDialog = () => {
        this.setState({isAddInputTestCaseFileVisible: true});
    };

    handleCloseAddInputTestCaseFileDialog = () => {
        this.setState({isAddInputTestCaseFileVisible: false});
    };

    handleOpenAddOutputTestCaseFileDialog = () => {
        this.setState({isAddOutputTestCaseFileVisible: true});
    };

    handleCloseAddOutputTestCaseFileDialog = () => {
        this.setState({isAddOutputTestCaseFileVisible: false});
    };

    handleEditTestName = () => {
        //TODO: do action
    };

    handleDeleteTestCase = (event) => {
        event.preventDefault();

        Api.deleteStageTestCase(this.props.projectName, this.props.stageName, this.props.testCase.testCaseName)
            .then(this.props.stageChangedHandler)
            .catch(function (error) {
                console.log(error);
            });
    };

    render() {
        return (
            <TableRow>
                <CustomTableCell component="th" scope="row">
                    <EditItemComponent
                        header={this.props.testCase.testCaseName}
                        info="Edytuj nazwę testu"
                        editActionHandler={this.handleEditTestName}/>
                </CustomTableCell>
                <CustomTableCell>
                    <EditItemComponent
                        header={this.props.testCase.parameters ? this.props.testCase.parameters : 'Brak'}
                        info="Edytuj parametry"
                        editActionHandler={this.handleOpenEditTestCaseParametersDialog}/>
                    <EditParametersDialog isOpen={this.state.isEditTestCaseParametersVisible}
                                          closeActionHandler={this.handleCloseEditTestCaseParametersDialog}
                                          successActionHandler={this.props.stageChangedHandler}
                                          projectName={this.props.projectName}
                                          stageName={this.props.stageName}
                                          testCaseName={this.props.testCase.testCaseName}
                                          headerText="Dodaj parametry uruchomienia"
                                          defaultValue={this.props.testCase.parameters}/>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row">
                    <UploadAndDownloadItemComponent
                        header={this.props.testCase.inputFileName}
                        uploadInfo="Załaduj plik wejściowy"
                        uploadActionHandler={this.handleOpenAddInputTestCaseFileDialog}
                        downloadInfo="Pobierz plik wejściowy"
                        downloadDisabled={this.props.testCase.inputFileName === null}
                        downloadActionHandler={this.handleDownloadInputFile}/>
                    <UploadTestCaseFileDialog isOpen={this.state.isAddInputTestCaseFileVisible}
                                              closeActionHandler={this.handleCloseAddInputTestCaseFileDialog}
                                              successActionHandler={this.props.stageChangedHandler}
                                              projectName={this.props.projectName}
                                              stageName={this.props.stageName}
                                              testCaseName={this.props.testCase.testCaseName}
                                              fileType="input"
                                              headerText="Dodaj plik wejściowy"/>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row">
                    <UploadAndDownloadItemComponent
                        header={this.props.testCase.outputFileName}
                        uploadInfo="Załaduj plik wyjściowy"
                        uploadActionHandler={this.handleOpenAddOutputTestCaseFileDialog}
                        downloadInfo="Pobierz plik wyjściowy"
                        downloadDisabled={this.props.testCase.outputFileName === null}
                        downloadActionHandler={this.handleDownloadOutputFile}/>
                    <UploadTestCaseFileDialog isOpen={this.state.isAddOutputTestCaseFileVisible}
                                              closeActionHandler={this.handleCloseAddOutputTestCaseFileDialog}
                                              successActionHandler={this.props.stageChangedHandler}
                                              projectName={this.props.projectName}
                                              stageName={this.props.stageName}
                                              testCaseName={this.props.testCase.testCaseName}
                                              fileType="output"
                                              headerText="Dodaj plik wyjściowy"/>
                </CustomTableCell>
                <CustomTableCell component="th" scope="row">
                    <DeleteItemComponent info="Usuń przypadek testowy"
                                         deleteActionHandler={this.handleDeleteTestCase}/>
                </CustomTableCell>
                <CustomTableCell/>
            </TableRow>
        );
    }
}

export default (TestCaseRow);
