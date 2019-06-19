import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import {styles} from "../../../../../styles/ProjectBoardStyles";
import * as Api from "../../../../../Api";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Button from "@material-ui/core/Button/Button";
import Dialog from "@material-ui/core/Dialog/Dialog";
import InputWrapper from "../../../../../utils/InputWrapper";
import TextField from "@material-ui/core/TextField";

class EditStageEndDateDialog extends Component {

    constructor(props) {
        super(props);
        this.state = {
            endDate: "2019-09-15"
        };
    }

    handleEndDateAdded = () => event => {
        this.setState({endDate: event.target.value})
    };

    handleEditStageEndDate = (event) => {
        event.preventDefault();

        const data = new FormData();
        data.append('projectName', this.props.projectName);
        data.append('stageName', this.props.stageName);
        data.append('endDate', this.state.endDate);

        Api.editStageEndDate(data)
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
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.props.closeActionHandler} color="primary">
                            Anuluj
                        </Button>
                        <Button onClick={this.handleEditStageEndDate} color="primary">
                            Dodaj
                        </Button>
                    </DialogActions>
                </Dialog>
            </InputWrapper>
        );
    }
}

export default withStyles(styles)(EditStageEndDateDialog);
