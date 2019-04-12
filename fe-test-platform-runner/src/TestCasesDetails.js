import Button from "@material-ui/core/Button/Button";
import React, {Component} from "react";
import {withStyles} from "@material-ui/core";
import {styles} from "./styles/ProjectBoardStyles";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import TextField from "@material-ui/core/TextField/TextField";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Dialog from "@material-ui/core/Dialog/Dialog";
import backendUrl from "./backendUrl";
import axios from "axios";

class TestCasesDetails extends Component {

    constructor(props) {
        super(props);
        this.state = {
            testCases: {testCases: []},
            isNewTestDialogVisible: false,
            newTestName: null,
            inputFile: null,
            outputFile: null
        };
        this.state.testCases.testCases = this.props.testCases;
    }

    fetchTestCases = () => {
        fetch(backendUrl(`/${this.props.projectName}/${this.props.stageName}/testCases`), {
            method: "GET",
            credentials: "include"
        })
            .then(response => response.json())
            .then(json => this.setState({
                testCases: json
            }))
    };

    handleOpenNewTestDialog = () => {
        this.setState({isNewTestDialogVisible: true});
    };

    handleCloseNewTestDialog = () => {
        this.setState({isNewTestDialogVisible: false});
    };

    handleAddNewTestParam = name => event => {
        this.setState({[name]: event.target.value});
    };

    handleAddNewTest = (ev) => {
        ev.preventDefault();

        const data = new FormData();
        data.append('projectName', this.props.projectName);
        data.append('stageName', this.props.stageName);
        data.append('testCaseName', this.state.newTestName);
        data.append('input', this.inputFile.files[0]);
        data.append('output', this.outputFile.files[0]);

        axios.post(backendUrl("/testCase"), data)
            .then(function (response) {
                console.log("success");
            })
            .then(this.fetchTestCases)
            .then(this.handleCloseNewTestDialog)
            .catch(function (error) {
                console.log(error);
            });
    };

    render() {
        return (
            <div className={this.props.classes.panel}>
                {this.state.testCases.testCases.map(testCase => (
                        <p className={this.props.classes.testCase}> {testCase} </p>
                    )
                )}
                <Button className={this.props.classes.button}
                        onClick={this.handleOpenNewTestDialog}>
                    Dodaj nowy test
                </Button>
                <Dialog open={this.state.isNewTestDialogVisible} onClose={this.handleCloseNewTestDialog}
                        aria-labelledby="form-dialog-title">
                    <DialogTitle id="form-dialog-title">Dodaj nowy test</DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            Podaj nazwę testu oraz pliki:
                        </DialogContentText>
                        <TextField
                            id="standard-name"
                            label="Nazwa testu"
                            className={this.props.classes.textField}
                            value={this.state.newTestName}
                            onChange={this.handleAddNewTestParam("newTestName")}
                            margin="normal"
                        />
                        {/*TODO: FIX: https://stackoverflow.com/questions/40589302/how-to-enable-file-upload-on-reacts-material-ui-simple-input*/}
                        <div className="form-group">
                            <input className="form-control" ref={(ref) => {
                                this.inputFile = ref;
                            }} type="file"/>
                        </div>
                        <div className="form-group">
                            <input className="form-control" ref={(ref) => {
                                this.outputFile = ref;
                            }} type="file"/>
                        </div>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleCloseNewTestDialog} color="primary">
                            Anuluj
                        </Button>
                        <Button onClick={this.handleAddNewTest} color="primary">
                            Dodaj
                        </Button>
                    </DialogActions>
                </Dialog>
            </div>
        );
    }
}

export default withStyles(styles)(TestCasesDetails);
