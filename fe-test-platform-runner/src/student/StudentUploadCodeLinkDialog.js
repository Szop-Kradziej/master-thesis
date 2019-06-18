import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import {styles} from "../styles/ProjectBoardStyles";
import * as Api from "../Api";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Button from "@material-ui/core/Button/Button";
import Dialog from "@material-ui/core/Dialog/Dialog";
import TextField from "@material-ui/core/TextField";

class UploadProjectDescriptionDialog extends Component {

    constructor(props) {
        super(props);
        this.state = {
            codeLink: null
        };
    }

    handleUploadCodeLink = (event) => {
        event.preventDefault();

        const data = new FormData();
        data.append('codeLink', this.state.codeLink);
        data.append('projectName', this.props.projectName);
        data.append('stageName', this.props.stageName);

        Api.uploadStudentCodeLink(data)
            .then(this.props.closeActionHandler)
            .then(this.props.successActionHandler)
            .catch(function (error) {
                console.log(error);
            });
    };

    handleCodeLinkAdded = () => event => {
        this.setState({codeLink: event.target.value})
    };

    render() {
        return (

            <Dialog open={this.props.isOpen} onClose={this.props.closeActionHandler}
                    aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">{this.props.headerText}</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        {this.props.headerText}
                    </DialogContentText>
                    <TextField
                        id="standard-name"
                        label="Adres kodu"
                        className={this.props.classes.textField}
                        value={this.state.codeLink}
                        onChange={this.handleCodeLinkAdded()}
                        margin="normal"/>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.props.closeActionHandler} color="primary">
                        Anuluj
                    </Button>
                    <Button onClick={this.handleUploadCodeLink} color="primary">
                        Dodaj
                    </Button>
                </DialogActions>
            </Dialog>
        );
    }
}

export default withStyles(styles)(UploadProjectDescriptionDialog);
