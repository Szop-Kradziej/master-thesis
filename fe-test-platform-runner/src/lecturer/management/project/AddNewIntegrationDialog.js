import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import Button from "@material-ui/core/Button/Button";
import TextField from "@material-ui/core/TextField/TextField";
import {styles} from "../../../styles/ProjectBoardStyles";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Dialog from "@material-ui/core/Dialog/Dialog";
import * as Api from "../../../Api";

class AddNewIntegrationDialog extends Component {

    constructor(props) {
        super(props);
        this.state = {integrationName: null};
    }

    handleIntegrationNameAdded = () => event => {
        this.setState({integrationName: event.target.value})
    };

    handleAddNewIntegration = () => {
        Api.addNewIntegration(this.props.projectName, this.state.integrationName)
            .then(this.props.closeActionHandler)
            .then(this.props.successActionHandler)
    };

    render() {
        return (
            <Dialog
                open={this.props.isOpen}
                onClose={this.props.closeActionHandler}
                aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">Dodaj nową integrację</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Podaj informacje o integracji:
                    </DialogContentText>
                    <TextField
                        id="standard-name"
                        label="Nazwa integracji"
                        className={this.props.classes.textField}
                        value={this.state.integrationName}
                        onChange={this.handleIntegrationNameAdded()}
                        margin="normal"/>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.props.closeActionHandler} color="primary">
                        Anuluj
                    </Button>
                    <Button onClick={this.handleAddNewIntegration} color="primary">
                        Dodaj
                    </Button>
                </DialogActions>
            </Dialog>
        );
    }
}

export default withStyles(styles)(AddNewIntegrationDialog);
