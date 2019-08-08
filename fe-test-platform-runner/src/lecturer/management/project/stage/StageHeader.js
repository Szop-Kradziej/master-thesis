import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import {CustomTableCell, styles} from "../../../../styles/ProjectBoardStyles";
import IconButton from "@material-ui/core/IconButton/IconButton";
import DeleteIcon from "@material-ui/icons/DeleteForever";
import EditItemComponent from "../../../../utils/EditItemComponent";
import UploadAndDownloadItemComponent from "../../../../utils/UploadAndDownloadItemComponent";
import * as Api from "../../../../Api";
import UploadStageDescriptionDialog from "./dialog/UploadStageDescriptionDialog";
import EditStageStartDateDialog from "./dialog/EditStageStartDateDialog";
import EditStageEndDateDialog from "./dialog/EditStageEndDateDialog";

class StageHeader extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isAddDescriptionDialogVisible: false,
            isEditStartDateDialogVisible: false,
            isEditEndDateDialogVisible: false,
        };
    }

    handleEditStageName = () => {
        //TODO: do action
    };

    handleOpenAddDescriptionDialog = () => {
        this.setState({isAddDescriptionDialogVisible: true});
    };

    handleCloseAddDescriptionDialog = () => {
        this.setState({isAddDescriptionDialogVisible: false});
    };

    handleOpenEditStartDateDialog = () => {
        this.setState({isEditStartDateDialogVisible: true});
    };

    handleCloseEditStartDateDialog = () => {
        this.setState({isEditStartDateDialogVisible: false});
    };

    handleOpenEditEndDateDialog = () => {
        this.setState({isEditEndDateDialogVisible: true});
    };

    handleCloseEditEndDateDialog = () => {
        this.setState({isEditEndDateDialogVisible: false});
    };

    handleDownloadStageDescription = () => {
        Api.downloadStageDescription(this.props.projectName, this.props.stage.stageName)
    };

    handleDeleteStage = () => {
        Api.deleteStage(this.props.projectName, this.props.stage.stageName)
            .then(this.props.stageChangedHandler)
            .catch(function (error) {
                console.log(error);
            });
    };

    render() {
        return (
            <div>
                <Table>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell width="15%">
                                <EditItemComponent
                                    header="Nazwa etapu:"
                                    info="Edytuj nazwę etapu"
                                    editActionHandler={this.handleEditStageName}/>
                            </CustomTableCell>
                            <CustomTableCell width="15%">
                                <UploadAndDownloadItemComponent
                                    header="Opis etapu:"
                                    uploadInfo="Załaduj opis etapu"
                                    uploadActionHandler={this.handleOpenAddDescriptionDialog}
                                    downloadInfo="Pobierz opis etapu"
                                    downloadDisabled={this.props.stage.stageDescription === null}
                                    downloadActionHandler={this.handleDownloadStageDescription}/>
                            </CustomTableCell>
                            <CustomTableCell width="35%"/>
                            <CustomTableCell width="15%">
                                <EditItemComponent
                                    header="Rozpoczęcie:"
                                    info="Edytuj datę rozpoczęcia etapu"
                                    editActionHandler={this.handleOpenEditStartDateDialog}/>
                            </CustomTableCell>
                            <CustomTableCell width="15%">
                                <EditItemComponent
                                    header="Zakończenie:"
                                    info="Edytuj datę zakończenia etapu"
                                    editActionHandler={this.handleOpenEditEndDateDialog}/>
                            </CustomTableCell>
                            <CustomTableCell>
                                <IconButton aria-label="Usuń" onClick={this.handleDeleteStage}>
                                    <DeleteIcon/>
                                </IconButton>
                            </CustomTableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        <CustomTableCell component="th" scope="row">
                            {this.props.stage.stageName}
                        </CustomTableCell>
                        <CustomTableCell component="th" scope="row">
                            {this.props.stage.stageDescription ? this.props.stage.stageDescription : 'Brak'}
                        </CustomTableCell>
                        <CustomTableCell component="th" scope="row"/>
                        <CustomTableCell component="th" scope="row">
                            {this.props.stage.startDate ? this.props.stage.startDate : 'Brak'}
                        </CustomTableCell>
                        <CustomTableCell component="th" scope="row">
                            {this.props.stage.endDate ? this.props.stage.endDate : 'Brak'}
                        </CustomTableCell>
                        <CustomTableCell component="th" scope="row"/>
                    </TableBody>
                </Table>
                <UploadStageDescriptionDialog isOpen={this.state.isAddDescriptionDialogVisible}
                                              closeActionHandler={this.handleCloseAddDescriptionDialog}
                                              successActionHandler={this.props.stageChangedHandler}
                                              projectName={this.props.projectName}
                                              stageName={this.props.stage.stageName}
                                              headerText="Dodaj opis etapu"/>
                <EditStageStartDateDialog isOpen={this.state.isEditStartDateDialogVisible}
                                          closeActionHandler={this.handleCloseEditStartDateDialog}
                                          successActionHandler={this.props.stageChangedHandler}
                                          projectName={this.props.projectName}
                                          stageName={this.props.stage.stageName}
                                          headerText="Edytuj datę rozpoczęcia etapu"/>
                <EditStageEndDateDialog isOpen={this.state.isEditEndDateDialogVisible}
                                        closeActionHandler={this.handleCloseEditEndDateDialog}
                                        successActionHandler={this.props.stageChangedHandler}
                                        projectName={this.props.projectName}
                                        stageName={this.props.stage.stageName}
                                        headerText="Edytuj datę zakończenia etapu"/>
            </div>
        );
    }
}

export default withStyles(styles)(StageHeader);
