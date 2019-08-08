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
        this.state = {stageName: null, startDate: "2019-06-19", endDate: "2019-09-15"};
    }

    handleStageNameAdded = () => event => {
        this.setState({stageName: event.target.value})
    };

    handleStartDateAdded = () => event => {
        this.setState({startDate: event.target.value})
    };

    handleEndDateAdded = () => event => {
        this.setState({endDate: event.target.value})
    };

    handleAddNewStage = () => {
        Api.addNewStage(this.props.projectName, this.state.stageName, this.state.startDate, this.state.endDate)
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
                        Podaj informacje o etapie:
                    </DialogContentText>
                    <TextField
                        id="standard-name"
                        label="Nazwa etapu"
                        className={this.props.classes.textField}
                        value={this.state.stageName}
                        onChange={this.handleStageNameAdded()}
                        margin="normal"/>
                    <div>
                        <TextField
                            id="start-date"
                            label="Data rozpoczęcia"
                            type="date"
                            defaultValue="2019-06-19"
                            className={this.props.classes.textField}
                            value={this.state.startDate}
                            onChange={this.handleStartDateAdded()}
                            InputLabelProps={{
                                shrink: true,
                            }}/>
                        <TextField
                            id="end-date"
                            label="Data zakończenia"
                            type="date"
                            defaultValue="2019-09-15"
                            className={this.props.classes.textField}
                            value={this.state.endDate}
                            onChange={this.handleEndDateAdded()}
                            InputLabelProps={{
                                shrink: true,
                            }}/>
                    </div>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.props.closeActionHandler} color="primary">
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
