import React from "react";
import Button from "@material-ui/core/Button/Button";
import TextField from "@material-ui/core/TextField/TextField";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import Dialog from "@material-ui/core/Dialog/Dialog";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import * as Api from "../../Api";

class AddNewProjectDialog extends React.Component {

    constructor(props) {
        super(props);
        this.state = {newProjectName: null};
        this.handleAddNewProject = this.handleAddNewProject.bind(this)
    }

    handleNewProjectNameAdded = () => event => {
        this.setState({newProjectName: event.target.value})
    };

    handleAddNewProject = () => {
        Api.addNewProject(this.state.newProjectName)
            .then(this.props.closeActionHandler)
            .then(this.props.successActionHandler)
    };

    render() {
        return (
            <Dialog
                open={this.props.isOpen}
                onClose={this.props.closeActionHandler}
                aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">
                    Dodaj nowy projekt
                </DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Podaj nazwę projektu:
                    </DialogContentText>
                    <TextField
                        id="standard-name"
                        label="Nazwa projektu"
                        value={this.state.newProjectName}
                        onChange={this.handleNewProjectNameAdded()}
                        margin="normal"
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.props.closeActionHandler} color="primary">
                        Anuluj
                    </Button>
                    <Button onClick={this.handleAddNewProject} color="primary">
                        Dodaj
                    </Button>
                </DialogActions>
            </Dialog>
        );
    }
}

export default AddNewProjectDialog