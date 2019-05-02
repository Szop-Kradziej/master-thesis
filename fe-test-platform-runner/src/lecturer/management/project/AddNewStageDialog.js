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

class AddNewStageDialog extends Component {

    constructor(props) {
        super(props);
        this.state = {newStageName: null};
    }

    handleNewStageNameAdded = () => event => {
        this.setState({newStageName: event.target.value})
    };

    handleAddNewStage = () => {
        Api.addNewStage(this.props.projectName, this.state.newStageName)
            .then(this.props.closeActionHandler)
            .then(this.props.successActionHandler)
    };

    render() {
        return (
            <Dialog
                open={this.props.isOpen}
                onClose={this.props.closeActionHandler}
                aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">Dodaj nowy etap</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Podaj nazwę etapu:
                    </DialogContentText>
                    <TextField
                        id="standard-name"
                        label="Nazwa etapu"
                        className={this.props.classes.textField}
                        value={this.state.newStageName}
                        onChange={this.handleNewStageNameAdded()}
                        margin="normal"
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.closeActionHandler} color="primary">
                        Anuluj
                    </Button>
                    <Button onClick={this.handleAddNewStage} color="primary">
                        Dodaj
                    </Button>
                </DialogActions>
            </Dialog>
        );
    }
}

export default withStyles(styles)(AddNewStageDialog);
