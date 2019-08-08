import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import {styles} from "../../../styles/ProjectBoardStyles";
import * as Api from "../../../Api";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Button from "@material-ui/core/Button/Button";
import Dialog from "@material-ui/core/Dialog/Dialog";

class PreviewStudentUploadReportDialog extends Component {

    constructor(props) {
        super(props);
        this.state = {
            inputFile: null
        };
    }

    handleUploadStudentReport = (event) => {
        event.preventDefault();

        const data = new FormData();
        data.append('file', this.inputFile.files[0]);
        data.append('projectName', this.props.projectName);
        data.append('stageName', this.props.stageName);

        Api.uploadPreviewStudentReport(this.props.groupName, data)
            .then(this.props.closeActionHandler)
            .then(this.props.successActionHandler)
            .catch(function (error) {
                console.log(error);
            });
    };

    render() {
        return (
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
                            this.inputFile = ref;
                        }} type="file"/>
                    </div>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.props.closeActionHandler} color="primary">
                        Anuluj
                    </Button>
                    <Button onClick={this.handleUploadStudentReport} color="primary">
                        Dodaj
                    </Button>
                </DialogActions>
            </Dialog>
        );
    }
}

export default withStyles(styles)(PreviewStudentUploadReportDialog);
