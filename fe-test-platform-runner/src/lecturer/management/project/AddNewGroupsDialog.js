import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import Button from "@material-ui/core/Button/Button";
import {styles} from "../../../styles/ProjectBoardStyles";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Dialog from "@material-ui/core/Dialog/Dialog";
import * as Api from "../../../Api";
import InputWrapper from "../../../utils/InputWrapper";

class AddNewGroupsDialog extends Component {

    constructor(props) {
        super(props);
        this.state = {
            inputGroupsFile: null
        };
    }

    handleUploadGroupsFile = (event) => {
        event.preventDefault();

        const data = new FormData();
        data.append('file', this.inputGroupsFile.files[0]);

        Api.uploadGroupsFile(this.props.projectName, data)
            .then(this.props.closeActionHandler)
            .then(this.props.successActionHandler)
            .catch(function (error) {
                console.log(error);
            });
    };

    render() {
        return (
            <InputWrapper>
                <Dialog open={this.props.isOpen} onClose={this.props.closeActionHandler}
                        aria-labelledby="form-dialog-title">
                    <DialogTitle id="form-dialog-title">{this.props.headerText}</DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            Podaj ścieżkę pliku:
                        </DialogContentText>
                        {/*TODO: FIX: https://stackoverflow.com/questions/40589302/how-to-enable-file-upload-on-reacts-material-ui-simple-input*/}
                        <div className="form-group">
                            <input className="form-control" ref={(ref) => {
                                this.inputGroupsFile = ref;
                            }} type="file"/>
                        </div>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.props.closeActionHandler} color="primary">
                            Anuluj
                        </Button>
                        <Button onClick={this.handleUploadGroupsFile} color="primary">
                            Dodaj
                        </Button>
                    </DialogActions>
                </Dialog>
            </InputWrapper>
        );
    }
}

export default withStyles(styles)(AddNewGroupsDialog);
