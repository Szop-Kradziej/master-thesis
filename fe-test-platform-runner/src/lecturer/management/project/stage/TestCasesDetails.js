import Button from "@material-ui/core/Button/Button";
import React, {Component} from "react";
import {withStyles} from "@material-ui/core";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import TextField from "@material-ui/core/TextField/TextField";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Dialog from "@material-ui/core/Dialog/Dialog";
import backendUrl from "../../../../backendUrl";
import axios from "axios";
import TestCaseRow from "./TestCaseRow";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import {CustomTableCell} from "../../../../styles/ProjectBoardStyles";
import Table from "@material-ui/core/Table/Table";
import TableBody from "@material-ui/core/es/TableBody/TableBody";
import AddNewItemComponent from "../../../../utils/AddNewItemComponent";

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
                <AddNewItemComponent
                    header="Testy"
                    info="Dodaj nowy test"
                    addActionHandler={this.handleOpenNewTestDialog}/>
                <Table>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell width="20%">
                                Nazwa testu:
                            </CustomTableCell>
                            <CustomTableCell width="15%">
                                Plik wejściowy
                            </CustomTableCell>
                            <CustomTableCell width="15%">
                                Plik wyjściowy
                            </CustomTableCell>
                            <CustomTableCell width="5%"/>
                            <CustomTableCell/>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.testCases.testCases.map(testCase => (
                                <TestCaseRow
                                    testCase={testCase}
                                    projectName={this.props.projectName}
                                    stageName={this.props.stageName}/>
                            )
                        )}
                    </TableBody>
                </Table>
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

const styles = theme => ({
    root: {
        flexGrow: 1,
    },
});

export default withStyles(styles)(TestCasesDetails);
