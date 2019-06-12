import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import {styles} from "../../../styles/ProjectBoardStyles";
import EditItemComponent from "../../../utils/EditItemComponent";
import UploadAndDownloadItemComponent from "../../../utils/UploadAndDownloadItemComponent";
import * as Api from "../../../Api";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Button from "@material-ui/core/Button/Button";
import Dialog from "@material-ui/core/Dialog/Dialog";

class ProjectHeader extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isAddDescriptionDialogVisible: false,
            inputDescriptionFile: null
        };
    }

    handleEditProjectName = () => {
        //TODO: do action
    };


    handleUploadProjectDescription = (event) => {
        event.preventDefault();

        const data = new FormData();
        data.append('file', this.inputDescriptionFile.files[0]);
        data.append('projectName', this.props.projectName);

        Api.uploadProjectDescription(data)
            .then(this.handleCloseAddDescriptionDialog)
            .then(this.props.projectChangedHandler)
            .catch(function (error) {
                console.log(error);
            });
    };

    handleOpenAddDescriptionDialog = () => {
        this.setState({isAddDescriptionDialogVisible: true});
    };

    handleCloseAddDescriptionDialog = () => {
        this.setState({isAddDescriptionDialogVisible: false});
    };

    handleDownloadProjectDescription = () => {
        Api.downloadProjectDescription(this.props.projectName)

    };

    render() {
        return (
            <div display="block">
                <EditItemComponent
                    header={"Nazwa projektu: " + this.props.projectName}
                    info="Edytuj nazwę projektu"
                    editActionHandler={this.handleEditProjectName}/>
                <UploadAndDownloadItemComponent className={this.props.classes.projectDescription}
                                                header={"Opis projektu: " + (this.props.projectDescription ? this.props.projectDescription : "Brak")}
                                                uploadInfo="Załaduj opis projektu"
                                                uploadActionHandler={this.handleOpenAddDescriptionDialog}
                                                downloadInfo="Pobierz opis projektu"
                                                downloadDisabled={this.props.projectDescription === null}
                                                downloadActionHandler={this.handleDownloadProjectDescription}/>
                <Dialog open={this.state.isAddDescriptionDialogVisible} onClose={this.handleCloseAddDescriptionDialog}
                        aria-labelledby="form-dialog-title">
                    <DialogTitle id="form-dialog-title">Dodaj opis projektu</DialogTitle>
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
                        <Button onClick={this.handleUploadProjectDescription} color="primary">
                            Dodaj
                        </Button>
                    </DialogActions>
                </Dialog>
            </div>
        );
    }
}

export default withStyles(styles)(ProjectHeader);
