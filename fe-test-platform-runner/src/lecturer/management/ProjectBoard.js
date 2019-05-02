import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
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
import IconButton from "@material-ui/core/IconButton/IconButton";
import DeleteIcon from "@material-ui/icons/DeleteForever";
import AddNewItemComponent from "../../utils/AddNewItemComponent";
import EditItemComponent from "../../utils/EditItemComponent";
import UploadAndDownloadItemComponent from "../../utils/UploadAndDownloadItemComponent";
import * as Api from "../../Api";

class ProjectBoard extends Component {

    constructor(props) {
        super(props);
        this.state = {stages: {stages: []}, isNewStageDialogVisible: false, newStageName: null};
    }

    componentDidMount() {
        this.fetchStages();
    }

    fetchStages = () => {
        Api.fetchStages(this.props.match.params.projectId)
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

    handleEditProjectName = () => {
        //    TODO: do action
    };

    handleEditStageName = () => {
        //TODO: do action
    };

    handleEditStageDeadline = () => {
        //TODO: do action
    };

    handleUploadProjectDescription = () => {
        //TODO: do action
    };

    handleDownloadProjectDescription = () => {
        //TODO: do action
    };

    handleUploadStageDescription = () => {
        //TODO: do action
    };

    handleDownloadStageDescription = () => {
        //TODO: do action
    };

    handleAddNewStage = () => {
        Api.addNewStage(this.props.match.params.projectId, this.state.newStageName)
            .then(() => this.setState({isNewStageDialogVisible: false, newStageName: null}))
            .then(this.fetchStages)
    };

    render() {
        return (
            <div className={this.props.classes.app}>
                <div display="block">
                    <EditItemComponent
                        header={"Nazwa projektu: " + this.props.match.params.projectId}
                        info="Edytuj nazwę projektu"
                        editActionHandler={this.handleEditProjectName}/>
                    <UploadAndDownloadItemComponent className={this.props.classes.projectDescription}
                                                    header="Opis projektu: TODO: Opis projektu"
                                                    uploadInfo="Załaduj opis projektu"
                                                    uploadActionHandler={this.handleUploadProjectDescription}
                                                    downloadInfo="Pobierz opis projektu"
                                                    downloadActionHandler={this.handleDownloadProjectDescription}/>
                </div>
                <Table className={this.props.classes.table}>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell>
                                <AddNewItemComponent
                                    header="Etapy"
                                    info="Dodaj nowy etap"
                                    addActionHandler={this.handleOpenNewStageDialog}/>
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
                                                <Table>
                                                    <TableHead>
                                                        <TableRow>
                                                            <CustomTableCell width="15%">
                                                                <EditItemComponent
                                                                    header="Nazwa etapu:"
                                                                    info="Edytuj nazwę etapu"
                                                                    editActionHandler={this.handleEditStageName}/>
                                                            </CustomTableCell>
                                                            <CustomTableCell width="15%">
                                                                <UploadAndDownloadItemComponent
                                                                    header="Opis etapu:"
                                                                    uploadInfo="Załaduj opis etapu"
                                                                    uploadActionHandler={this.handleUploadStageDescription}
                                                                    downloadInfo="Pobierz opis etapu"
                                                                    downloadActionHandler={this.handleDownloadStageDescription}/>
                                                            </CustomTableCell>
                                                            <CustomTableCell width="50%"/>
                                                            <CustomTableCell width="15%">
                                                                <EditItemComponent
                                                                    header="Deadline:"
                                                                    info="Edytuj deadline etapu"
                                                                    editActionHandler={this.handleEditStageDeadline}/>
                                                            </CustomTableCell>
                                                            <CustomTableCell>
                                                                <IconButton aria-label="Usuń">
                                                                    <DeleteIcon/>
                                                                </IconButton>
                                                            </CustomTableCell>
                                                        </TableRow>
                                                    </TableHead>
                                                    <TableBody>
                                                        <CustomTableCell component="th" scope="row">
                                                            {stage.stageName}
                                                        </CustomTableCell>
                                                        <CustomTableCell component="th" scope="row">
                                                            TODO: Plik z opisem
                                                        </CustomTableCell>
                                                        <CustomTableCell component="th" scope="row"/>
                                                        <CustomTableCell component="th" scope="row">
                                                            TODO: Deadline
                                                        </CustomTableCell>
                                                        <CustomTableCell component="th" scope="row"/>
                                                    </TableBody>
                                                </Table>
                                            </Typography>
                                        </ExpansionPanelSummary>
                                        <ExpansionPanelDetails>
                                            <Typography className={this.props.classes.heading}>
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
