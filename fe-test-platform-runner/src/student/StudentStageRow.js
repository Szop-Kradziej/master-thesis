import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import backendUrl from "../backendUrl";
import Typography from '@material-ui/core/Typography';
import Button from "@material-ui/core/Button/Button";
import axios from "axios";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Dialog from "@material-ui/core/Dialog/Dialog";

class StudentStageRow extends Component {

    constructor(props) {
        super(props);
        this.state = {isAddBinaryDialogVisible: false, inputFile: null, binaryName: 'Brak pliku'};
    }

    handleOpenAddBinaryDialog = () => {
        this.setState({isAddBinaryDialogVisible: true});
    };

    handleCloseAddBinaryDialog = () => {
        this.setState({isAddBinaryDialogVisible: false});
    };

    handleBinaryFileSaved = () => {
        this.setState({binaryName: this.inputFile.files[0].name})
    };

    handleAddBinary = (event) => {
        event.preventDefault();

        const data = new FormData();
        data.append('file', this.inputFile.files[0]);
        data.append('projectName', this.props.projectName);
        data.append('stageName', this.props.stageName);

        axios.post(backendUrl("/upload"), data)
            .then(function (response) {
                console.log("success");
            })
            .then(this.handleBinaryFileSaved)
            .then(this.handleCloseAddBinaryDialog)
            .catch(function (error) {
                console.log(error);
            });
    };

    handleRunTests = () => {
        fetch(backendUrl(`/run`), {
            method: "POST",
            credentials: "include",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: "projectName=" + this.props.projectName +"&stageName=" + this.props.stageName
        })
            .then( function (response){console.log(response)})
    };

    render() {
        return (
            <div className={this.props.classes.stageRow}>
                <Typography className={this.props.classes.heading}>
                    {this.props.stageName}
                </Typography>
                <p> {this.state.binaryName} </p>
                <InputWrapper>
                    <Button className={this.props.classes.button} onClick={this.handleOpenAddBinaryDialog}>
                        Dodaj binarkę
                    </Button>
                    <Button className={this.props.classes.button} onClick={this.handleRunTests}>
                        Uruchom testy
                    </Button>
                    <Dialog open={this.state.isAddBinaryDialogVisible} onClose={this.handleCloseAddBinaryDialog}
                            aria-labelledby="form-dialog-title">
                        <DialogTitle id="form-dialog-title">Dodaj nowy test</DialogTitle>
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
                            <Button onClick={this.handleCloseAddBinaryDialog} color="primary">
                                Anuluj
                            </Button>
                            <Button onClick={this.handleAddBinary} color="primary">
                                Dodaj
                            </Button>
                        </DialogActions>
                    </Dialog>
                </InputWrapper>
            </div>
        );
    }
}

const stopPropagation = (e) => e.stopPropagation();
const InputWrapper = ({children}) =>
    <div onClick={stopPropagation}>
        {children}
    </div>;

export const styles = (theme) => ({
    button: {
        backgroundColor: "#5aa724",
        color: "black",
        marginTop: 20
    },
    stageRow: {
        display: 'flex'
    },
    heading: {
        fontSize: theme.typography.pxToRem(15),
        fontWeight: theme.typography.fontWeightRegular,
    },
});

export default withStyles(styles)(StudentStageRow);
