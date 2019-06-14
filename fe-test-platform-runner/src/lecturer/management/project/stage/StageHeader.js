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
import UploadStageDescriptionDialog from "./UploadStageDescriptionDialog";

class StageHeader extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isAddDescriptionDialogVisible: false,
        };
    }

    handleEditStageName = () => {
        //TODO: do action
    };

    handleEditStageDeadline = () => {
        //TODO: do action
    };

    handleOpenAddDescriptionDialog = () => {
        this.setState({isAddDescriptionDialogVisible: true});
    };

    handleCloseAddDescriptionDialog = () => {
        this.setState({isAddDescriptionDialogVisible: false});
    };

    handleDownloadStageDescription = () => {
        Api.downloadStageDescription(this.props.projectName, this.props.stageName)
    };

    handleDeleteStage = () => {
        Api.deleteStage(this.props.projectName, this.props.stageName)
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
                                    downloadDisabled={this.props.stageDescription === null}
                                    downloadActionHandler={this.handleDownloadStageDescription}/>
                            </CustomTableCell>
                            <CustomTableCell width="50%"/>
                            <CustomTableCell width="15%">
                                <EditItemComponent
                                    header="Deadline:"
                                    info="Edytuj deadline etapu"
                                    editActionHandler={this.handleEditStageDeadline}/>
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
                            {this.props.stageName}
                        </CustomTableCell>
                        <CustomTableCell component="th" scope="row">
                            {this.props.stageDescription ? this.props.stageDescription : 'Brak'}
                        </CustomTableCell>
                        <CustomTableCell component="th" scope="row"/>
                        <CustomTableCell component="th" scope="row">
                            10/06/2019 23:59
                        </CustomTableCell>
                        <CustomTableCell component="th" scope="row"/>
                    </TableBody>
                </Table>
                <UploadStageDescriptionDialog isOpen={this.state.isAddDescriptionDialogVisible}
                                              closeActionHandler={this.handleCloseAddDescriptionDialog}
                                              successActionHandler={this.props.stageChangedHandler}
                                              projectName={this.props.projectName}
                                              stageName={this.props.stageName}
                                              headerText="Dodaj opis etapu"/>
            </div>
        );
    }
}

export default withStyles(styles)(StageHeader);
