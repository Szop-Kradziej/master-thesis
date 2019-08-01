import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import backendUrl from "../backendUrl";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import {CustomTableCell, styles} from "../styles/ProjectBoardStyles";
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import StudentTestCasesDetails from "./StudentTestCasesDetails"
import StudentStageRow from "./StudentStageRow";
import IconButton from "@material-ui/core/IconButton/IconButton";
import DescriptionIcon from "@material-ui/icons/Description";
import SettingsIcon from "@material-ui/icons/Settings";

import * as Api from "../Api";
import StudentIntegrationRow from "./StudentIntegrationRow";

class StudentProjectBoard extends Component {

    constructor(props) {
        super(props);
        this.state = {stages: {stages: []}, integrations: {integrations: []}};
    }

    componentDidMount() {
        this.fetchStages();
        this.fetchIntegrations();
    }

    fetchStages = () => {
        fetch(backendUrl(`/student/${this.props.match.params.projectId}/stages`), {
            method: "GET",
            credentials: "include"
        })
            .then(response => response.json())
            .then(json => this.setState({
                stages: json
            }))
    };

    fetchIntegrations = () => {
        fetch(backendUrl(`/student/${this.props.match.params.projectId}/integrations`), {
            method: "GET",
            credentials: "include"
        })
            .then(response => response.json())
            .then(json => this.setState({
                integrations: json
            }))
    };

    stageChangedHandler = () => {
        this.fetchStages()
    };

    integrationChangedHandler = () => {
        this.fetchIntegrations()
    };

    handleDownloadProjectDescription = () => {
        Api.downloadProjectDescription(this.props.match.params.projectId)
    };

    handleDownloadProjectEnvironment = () => {
        Api.downloadProjectEnvironment(this.props.match.params.projectId)
    };

    render() {
        return (
            <div className={this.props.classes.app}>
                <div>
                    {this.props.match.params.projectId}
                    <IconButton aria-label="Pobierz opis projektu" onClick={this.handleDownloadProjectDescription}>
                        <DescriptionIcon/>
                    </IconButton>
                    <IconButton aria-label="Pobierz konfigurację środowiska" onClick={this.handleDownloadProjectEnvironment}>
                        <SettingsIcon/>
                    </IconButton>
                </div>
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
                                    <ExpansionPanel disabled={!stage.enable}>
                                        <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                                            <StudentStageRow
                                                stage={stage}
                                                projectName={this.props.match.params.projectId}
                                                stageChangedHandler={this.stageChangedHandler}/>
                                        </ExpansionPanelSummary>
                                        <ExpansionPanelDetails>
                                            <Typography>
                                                <div className={this.props.classes.panel}>
                                                    <p className={this.props.classes.testsHeading}> Testy </p>
                                                    <StudentTestCasesDetails
                                                        testCases={stage.testCases}
                                                        projectName={this.props.match.params.projectId}
                                                        taskName={stage.stageName}
                                                        taskType="stage"/>
                                                </div>
                                            </Typography>
                                        </ExpansionPanelDetails>
                                    </ExpansionPanel>
                                </CustomTableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
                <Table className={this.props.classes.table}>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell>Integracje</CustomTableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.integrations.integrations.map(integration => (
                            <TableRow key={integration.integrationName}>
                                <CustomTableCell component="th" scope="row">
                                    <ExpansionPanel disabled={!integration.enable}>
                                        <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                                            <StudentIntegrationRow
                                                integration={integration}
                                                projectName={this.props.match.params.projectId}
                                                integrationChangedHandler={this.integrationChangedHandler}/>
                                        </ExpansionPanelSummary>
                                        <ExpansionPanelDetails>
                                            <Typography>
                                                <div className={this.props.classes.panel}>
                                                    <p className={this.props.classes.testsHeading}> Testy </p>
                                                    <StudentTestCasesDetails
                                                        testCases={integration.testCases}
                                                        projectName={this.props.match.params.projectId}
                                                        taskName={integration.integrationName}
                                                        taskType="integration"/>
                                                </div>
                                            </Typography>
                                        </ExpansionPanelDetails>
                                    </ExpansionPanel>
                                </CustomTableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>
        );
    }
}

export default withStyles(styles)(StudentProjectBoard);
