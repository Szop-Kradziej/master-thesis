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

class EditStageCommentDialog extends Component {

    constructor(props) {
        super(props);
        this.state = {
            comment: null
        };
    }

    handleCommentAdded = () => event => {
        this.setState({comment: event.target.value})
    };

    handleEditStageComment = (event) => {
        event.preventDefault();

        const data = new FormData();
        data.append('projectName', this.props.projectName);
        data.append('stageName', this.props.stageName);
        data.append('comment', this.state.comment);

        Api.editStageComment(data)
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
                            label="Komentarz"
                            className={this.props.classes.textField}
                            value={this.state.comment}
                            onChange={this.handleCommentAdded()}
                            margin="normal"/>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.props.closeActionHandler} color="primary">
                            Anuluj
                        </Button>
                        <Button onClick={this.handleEditStageComment} color="primary">
                            Dodaj
                        </Button>
                    </DialogActions>
                </Dialog>
            </InputWrapper>
        );
    }
}

export default withStyles(styles)(EditStageCommentDialog);
