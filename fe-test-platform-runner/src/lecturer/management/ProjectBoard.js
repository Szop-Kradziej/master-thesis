import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import backendUrl from "../../backendUrl";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import Button from "@material-ui/core/Button/Button";
import TextField from "@material-ui/core/TextField/TextField";
import {CustomTableCell, styles} from "../../styles/ProjectBoardStyles";
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogContentText from "@material-ui/core/es/DialogContentText/DialogContentText";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Dialog from "@material-ui/core/Dialog/Dialog";
import TestCasesDetails from "./TestCasesDetails"
import AddIcon from "@material-ui/icons/AddCircle";
import IconButton from "@material-ui/core/IconButton/IconButton";

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
        this.setState({isNewTestDialogVisible: true});
    };

    handleCloseNewStageDialog = () => {
        this.setState({isNewTestDialogVisible: false});
    };

    handleNewStageNameAdded = () => event => {
        this.setState({newTestName: event.target.value})
    };

    handleAddNewStage = () => {
        fetch(backendUrl(`/stage`), {
            method: "POST",
            credentials: "include",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: "projectName=" + this.props.match.params.projectId + "&stageName=" + this.state.newTestName
        })
            .then(() => this.setState({isNewTestDialogVisible: false, newTestName: null}))
            .then(this.fetchStages)
    };

    render() {
        return (
            <div className={this.props.classes.app}>
                <p>
                    Projekt: {this.props.match.params.projectId}
                </p>
                <Table className={this.props.classes.table}>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell>
                                Etapy
                                <IconButton aria-label="Dodaj nowy etap" onClick={this.handleOpenNewStageDialog}>
                                    <AddIcon/>
                                </IconButton>
                            </CustomTableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.stages.stages.map(stage => (
                            <TableRow key={stage.stageName}>
                                <CustomTableCell component="th" scope="row">
                                    <ExpansionPanel>
                                        <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                                            <Typography className={this.props.classes.heading}>
                                                {stage.stageName}
                                            </Typography>
                                        </ExpansionPanelSummary>
                                        <ExpansionPanelDetails>
                                            <Typography>
                                                <div className={this.props.classes.panel}>
                                                    <TestCasesDetails
                                                        testCases={stage.testCases}
                                                        projectName={this.props.match.params.projectId}
                                                        stageName={stage.stageName}
                                                    />
                                                </div>
                                            </Typography>
                                        </ExpansionPanelDetails>
                                    </ExpansionPanel>
                                </CustomTableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
                <Dialog open={this.state.isNewTestDialogVisible} onClose={this.handleCloseNewStageDialog}
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
                            value={this.state.newTestName}
                            onChange={this.handleNewStageNameAdded()}
                            margin="normal"
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleCloseNewStageDialog} color="primary">
                            Anuluj
                        </Button>
                        <Button onClick={this.handleAddNewStage} color="primary">
                            Dodaj
                        </Button>
                    </DialogActions>
                </Dialog>
            </div>
        );
    }
}

export default withStyles(styles)(ProjectBoard);
