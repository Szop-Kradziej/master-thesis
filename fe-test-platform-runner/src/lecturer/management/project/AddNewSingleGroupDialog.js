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

class AddNewSingleGroupDialog extends Component {

    constructor(props) {
        super(props);
        this.state = {newGroupName: null};
    }

    handleNewGroupNameAdded = () => event => {
        this.setState({newGroupName: event.target.value})
    };

    handleAddNewGroupName = () => {
        Api.addNewGroup(this.props.projectName, this.state.newGroupName)
            .then(this.props.closeActionHandler)
            .then(this.props.successActionHandler)
    };

    render() {
        return (
            <Dialog
                open={this.props.isOpen}
                onClose={this.props.closeActionHandler}
                aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">Dodaj nową grupę</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Podaj nazwę grupy:
                    </DialogContentText>
                    <TextField
                        id="standard-name"
                        label="Nazwa grupy"
                        className={this.props.classes.textField}
                        value={this.state.newGroupName}
                        onChange={this.handleNewGroupNameAdded()}
                        margin="normal"
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.props.closeActionHandler} color="primary">
                        Anuluj
                    </Button>
                    <Button onClick={this.handleAddNewGroupName} color="primary">
                        Dodaj
                    </Button>
                </DialogActions>
            </Dialog>
        );
    }
}

export default withStyles(styles)(AddNewSingleGroupDialog);
