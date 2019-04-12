import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import backendUrl from "./backendUrl";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import Button from "@material-ui/core/Button/Button";
import TextField from "@material-ui/core/TextField/TextField";
import {CustomTableCell, getModalStyle, styles} from "./styles/ProjectBoardStyles";
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

    handleOpenNewTestDialog = () => {

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
                            <CustomTableCell>Etapy</CustomTableCell>
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
                                                    {stage.testCases.map(testCase => (
                                                            <p className={this.props.classes.testCase}> {testCase} </p>
                                                        )
                                                    )}
                                                </div>
                                                <Button className={this.props.classes.button}
                                                        onClick={this.handleOpenNewTestDialog}>
                                                    Dodaj nowy test
                                                </Button>
                                            </Typography>
                                        </ExpansionPanelDetails>
                                    </ExpansionPanel>
                                </CustomTableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
                <Button className={this.props.classes.button} onClick={this.handleOpenNewStageDialog}>
                    Dodaj nowy etap
                </Button>
                <Dialog open={this.state.isNewStageDialogVisible} onClose={this.handleCloseNewStageDialog}
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
                            value={this.state.newStageName}
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
