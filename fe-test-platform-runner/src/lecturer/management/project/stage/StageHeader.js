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
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Button from "@material-ui/core/Button/Button";
import Dialog from "@material-ui/core/Dialog/Dialog";

class StageHeader extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isAddDescriptionDialogVisible: false,
            inputDescriptionFile: null
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

    handleUploadStageDescription = (event) => {
        event.preventDefault();

        const data = new FormData();
        data.append('file', this.inputDescriptionFile.files[0]);
        data.append('projectName', this.props.projectName);
        data.append('stageName', this.props.stageName);

        Api.uploadStageDescription(data)
            .then(this.handleCloseAddDescriptionDialog)
            .then(this.props.stageChangedHandler)
            .catch(function (error) {
                console.log(error);
            });
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
                <Dialog open={this.state.isAddDescriptionDialogVisible} onClose={this.handleCloseAddDescriptionDialog}
                        aria-labelledby="form-dialog-title">
                    <DialogTitle id="form-dialog-title">Dodaj opis etapu</DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            Podaj ścieżkę pliku:
                        </DialogContentText>
                        {/*TODO: FIX: https://stackoverflow.com/questions/40589302/how-to-enable-file-upload-on-reacts-material-ui-simple-input*/}
                        <div className="form-group">
                            <input className="form-control" ref={(ref) => {
                                this.inputDescriptionFile = ref;
                            }} type="file"/>
                        </div>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleCloseAddDescriptionDialog} color="primary">
                            Anuluj
                        </Button>
                        <Button onClick={this.handleUploadStageDescription} color="primary">
                            Dodaj
                        </Button>
                    </DialogActions>
                </Dialog>
            </div>
        );
    }
}

export default withStyles(styles)(StageHeader);
