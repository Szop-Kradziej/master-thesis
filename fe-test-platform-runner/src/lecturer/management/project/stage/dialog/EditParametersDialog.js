import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import {styles} from "../../../../../styles/ProjectBoardStyles";
import * as Api from "../../../../../Api";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Button from "@material-ui/core/Button/Button";
import Dialog from "@material-ui/core/Dialog/Dialog";
import InputWrapper from "../../../../../utils/InputWrapper";
import TextField from "@material-ui/core/TextField";

class EditParametersDialog extends Component {

    constructor(props) {
        super(props);
        this.state = {
            parameters: this.props.defaultValue
        };
    }

    handleParametersAdded = () => event => {
        this.setState({parameters: event.target.value})
    };

    handleEditTestCaseParameters = (event) => {
        event.preventDefault();

        const data = new FormData();
        data.append('projectName', this.props.projectName);
        data.append('stageName', this.props.stageName);
        data.append('testCaseName', this.props.testCaseName);
        data.append('parameters', this.state.parameters);

        Api.editStageTestCaseParameters(data)
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
                            id="standard-points"
                            label="Parametry"
                            className={this.props.classes.textField}
                            value={this.state.parameters}
                            onChange={this.handleParametersAdded()}
                            margin="normal"/>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.props.closeActionHandler} color="primary">
                            Anuluj
                        </Button>
                        <Button onClick={this.handleEditTestCaseParameters} color="primary">
                            Dodaj
                        </Button>
                    </DialogActions>
                </Dialog>
            </InputWrapper>
        );
    }
}

export default withStyles(styles)(EditParametersDialog);
