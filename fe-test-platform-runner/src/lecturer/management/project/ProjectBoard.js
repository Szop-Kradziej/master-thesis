import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import TableRow from "@material-ui/core/TableRow/TableRow";
import {CustomTableCell, styles} from "../../../styles/ProjectBoardStyles";
import AddNewItemComponent from "../../../utils/AddNewItemComponent";
import * as Api from "../../../Api";
import ProjectHeader from "./ProjectHeader";
import StageComponent from "./stage/StageComponent";
import AddNewStageDialog from "./AddNewStageDialog";
import GroupComponent from "./group/GroupComponent";
import AddNewGroupsDialog from "./AddNewGroupsDialog";
import AddNewSingleGroupDialog from "./AddNewSingleGroupDialog";
import AddAndUploadItemComponent from "../../../utils/AddAndUploadItemComponent";
import AddNewIntegrationDialog from "./AddNewIntegrationDialog";
import IntegrationComponent from "./integration/IntegrationComponent";
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';

class ProjectBoard extends Component {

    constructor(props) {
        super(props);
        this.state = {
            stages: {stages: []},
            groups: {groups: []},
            integrations: {integrations: []},
            isNewStageDialogVisible: false,
            isNewIntegrationDialogVisible: false,
            isNewSingleGroupDialogVisible: false,
            isNewGroupsDialogVisible: false
        };
    }

    componentDidMount() {
        this.fetchStages();
        this.fetchGroups();
        this.fetchIntegrations();
    }

    fetchStages = () => {
        Api.fetchStages(this.props.match.params.projectId)
            .then(response => response.json())
            .then(json => this.setState({
                stages: json
            }))
    };

    fetchGroups = () => {
        Api.fetchGroups(this.props.match.params.projectId)
            .then(response => response.json())
            .then(json => this.setState({
                groups: json
            }))
    };

    fetchIntegrations = () => {
        Api.fetchIntegrations(this.props.match.params.projectId)
            .then(response => response.json())
            .then(json => this.setState({
                integrations: json
            }))
    };

    handleOpenNewStageDialog = () => {
        this.setState({isNewStageDialogVisible: true});
    };

    handleCloseNewStageDialog = () => {
        this.setState({isNewStageDialogVisible: false});
    };

    handleOpenNewIntegrationDialog = () => {
        this.setState({isNewIntegrationDialogVisible: true});
    };

    handleCloseNewIntegrationDialog = () => {
        this.setState({isNewIntegrationDialogVisible: false});
    };

    handleOpenNewSingleGroupDialog = () => {
        this.setState({isNewSingleGroupDialogVisible: true});
    };

    handleCloseNewSingleGroupDialog = () => {
        this.setState({isNewSingleGroupDialogVisible: false});
    };

    handleOpenNewGroupsDialog = () => {
        this.setState({isNewGroupsDialogVisible: true});
    };

    handleCloseNewGroupsDialog = () => {
        this.setState({isNewGroupsDialogVisible: false});
    };

    render() {
        return (
            <div className={this.props.classes.app}>
                <ProjectHeader
                    display="block"
                    projectName={this.props.match.params.projectId}
                    projectDescription={this.state.stages.projectDescription}
                    projectEnvironment={this.state.stages.projectEnvironment}
                    projectChangedHandler={this.fetchStages}/>
                <ExpansionPanel>
                    <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                        <Typography className={this.props.classes.headingMainPanel}>
                            <AddNewItemComponent
                                header="Etapy"
                                info="Dodaj nowy etap"
                                addActionHandler={this.handleOpenNewStageDialog}/>
                        </Typography>
                    </ExpansionPanelSummary>
                    <ExpansionPanelDetails>
                        <Typography className={this.props.classes.heading}>
                            <div className={this.props.classes.panel}>
                                {this.state.stages.stages.map(stage => (
                                    <TableRow key={stage.stageName}>
                                        <CustomTableCell component="th" scope="row">
                                            <StageComponent
                                                stage={stage}
                                                projectName={this.props.match.params.projectId}
                                                stageChangedHandler={this.fetchStages}/>
                                        </CustomTableCell>
                                    </TableRow>
                                ))}
                            </div>
                        </Typography>
                    </ExpansionPanelDetails>
                </ExpansionPanel>
                <ExpansionPanel>
                    <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                        <Typography className={this.props.classes.headingMainPanel}>
                            <AddNewItemComponent
                                header="Integracje"
                                info="Dodaj nową integrację"
                                addActionHandler={this.handleOpenNewIntegrationDialog}/>
                        </Typography>
                    </ExpansionPanelSummary>
                    <ExpansionPanelDetails>
                        <Typography className={this.props.classes.heading}>
                            <div className={this.props.classes.panel}>
                                {this.state.integrations.integrations.map(integration => (
                                    <TableRow key={integration.name}>
                                        <CustomTableCell component="th" scope="row">
                                            <IntegrationComponent
                                                integration={integration}
                                                projectName={this.props.match.params.projectId}
                                                integrationChangedHandler={this.fetchIntegrations}/>
                                        </CustomTableCell>
                                    </TableRow>
                                ))}
                            </div>
                        </Typography>
                    </ExpansionPanelDetails>
                </ExpansionPanel>
                <ExpansionPanel>
                    <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                        <Typography className={this.props.classes.headingMainPanel}>
                            <AddAndUploadItemComponent
                                header="Grupy"
                                addInfo="Dodaj nową grupę"
                                addActionHandler={this.handleOpenNewSingleGroupDialog}
                                uploadInfo="Dodaj grupy z pliku"
                                uploadActionHandler={this.handleOpenNewGroupsDialog}/>
                        </Typography>
                    </ExpansionPanelSummary>
                    <ExpansionPanelDetails>
                        <Typography className={this.props.classes.heading}>
                            <div className={this.props.classes.panel}>
                                {this.state.groups.groups.map(group => (
                                    <TableRow key={group.name}>
                                        <CustomTableCell component="th" scope="row">
                                            <GroupComponent
                                                group={group}
                                                projectName={this.props.match.params.projectId}
                                                groupChangedHandler={this.fetchGroups}/>
                                        </CustomTableCell>
                                    </TableRow>
                                ))}
                            </div>
                        </Typography>
                    </ExpansionPanelDetails>
                </ExpansionPanel>
                <AddNewStageDialog
                    isOpen={this.state.isNewStageDialogVisible}
                    projectName={this.props.match.params.projectId}
                    closeActionHandler={this.handleCloseNewStageDialog}
                    successActionHandler={this.fetchStages}/>
                <AddNewIntegrationDialog
                    isOpen={this.state.isNewIntegrationDialogVisible}
                    projectName={this.props.match.params.projectId}
                    closeActionHandler={this.handleCloseNewIntegrationDialog}
                    successActionHandler={this.fetchIntegrations}
                    availableStages={this.state.stages.stages.map(stage => stage.stageName)}/>
                <AddNewSingleGroupDialog
                    isOpen={this.state.isNewSingleGroupDialogVisible}
                    projectName={this.props.match.params.projectId}
                    closeActionHandler={this.handleCloseNewSingleGroupDialog}
                    successActionHandler={this.fetchGroups}/>
                <AddNewGroupsDialog
                    isOpen={this.state.isNewGroupsDialogVisible}
                    projectName={this.props.match.params.projectId}
                    headerText="Dodaj grupy z pliku"
                    closeActionHandler={this.handleCloseNewGroupsDialog}
                    successActionHandler={this.fetchGroups}/>
            </div>
        );
    }
}

export default withStyles(styles)(ProjectBoard);
