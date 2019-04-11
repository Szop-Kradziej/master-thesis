import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import backendUrl from "./backendUrl";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import Button from "@material-ui/core/Button/Button";
import Modal from "@material-ui/core/Modal/Modal";
import TextField from "@material-ui/core/TextField/TextField";
import {styles, CustomTableCell, getModalStyle} from "./styles/ProjectBoardStyles";

class ProjectBoard extends Component {

    constructor(props) {
        super(props);
        this.state = {stages: {stages: []}, isNewStageDialogVisible: false, newStageName: null};
    }

    componentDidMount() {
        this.fetchStages();
    }

    fetchStages = () => {
        fetch(backendUrl(`/${this.props.match.params.projectId}/stages`), {
            method: "GET",
            credentials: "include"
        })
            .then(response => response.json())
            .then(json => this.setState({
                stages: json
            }))
    };

    handleOpenNewStageDialog = () => {
        this.setState({isNewStageDialogVisible: true});
    };

    handleCloseNewStageDialog = () => {
        this.setState({isNewStageDialogVisible: false});
    };

    handleNewStageNameAdded = () => event => {
        this.setState({newStageName: event.target.value})
    };

    handleAddNewStage = () => {
        fetch(backendUrl(`/stage`), {
            method: "POST",
            credentials: "include",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: "projectName=" + this.props.match.params.projectId + "&stageName=" + this.state.newStageName
        })
            .then(() => this.setState({isNewStageDialogVisible: false, newStageName: null}))
            .then(this.fetchStages)
    };

    render() {
        return (
            <div className={this.props.classes.app}>
                <p>
                    Nazwa projektu: {this.props.match.params.projectId}
                </p>
                <Table className={this.props.classes.table}>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell>Nazwa etapu</CustomTableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.stages.stages.map(stage => (
                            <TableRow key={stage.stageName}>
                                <CustomTableCell component="th" scope="row">
                                    {stage.stageName}
                                    {stage.testCases.map(testCase => (
                                            <p className={this.props.classes.testCase}> {testCase} </p>
                                        )
                                    )}
                                </CustomTableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
                <Button className={this.props.classes.button} onClick={this.handleOpenNewStageDialog}>
                    Dodaj nowy etap
                </Button>

                <Modal open={this.state.isNewStageDialogVisible}>
                    <div style={getModalStyle()} className={this.props.classes.paper}>
                        <form className={this.props.classes.container} noValidate autoComplete="off">
                            <TextField
                                id="standard-name"
                                label="Nazwa etapu"
                                className={this.props.classes.textField}
                                value={this.state.newStageName}
                                onChange={this.handleNewStageNameAdded()}
                                margin="normal"
                            />
                            <Button className={this.props.classes.button} onClick={this.handleAddNewStage}>
                                Dodaj
                            </Button>
                            {/*<Button className={this.props.classes.button} onClick={this.handleCloseNewProjectDialog()}>*/}
                            {/*Anuluj*/}
                            {/*</Button>*/}
                        </form>
                    </div>
                </Modal>
            </div>
        );
    }
}

export default withStyles(styles)(ProjectBoard);
