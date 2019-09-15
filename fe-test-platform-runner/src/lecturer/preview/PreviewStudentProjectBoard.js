import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import backendUrl from "../../backendUrl";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import {CustomHeaderCell, CustomTableCell, styles} from "../../styles/ProjectBoardStyles";
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import * as Api from "../../Api";
import PreviewStudentStageRow from "./PreviewStudentStageRow";
import PreviewStudentTestCasesDetails from "./PreviewStudentTestCasesDetails";
import PreviewStudentIntegrationRow from "./PreviewStudentIntegrationRow";
import {getAuthHeader} from "../../Api";
import DescriptionAndSettingsItemComponent from "../../utils/DescriptionAndSettingsItemComponent";
import PreviewStudentIntegrationTestCasesDetails from "./PreviewStudentIntegrationTestCasesDetails";

class PreviewStudentProjectBoard extends Component {

    constructor(props) {
        super(props);
        this.state = {stages: {stages: []}, integrations: {integrations: []}, students: []};
    }

    componentDidMount() {
        this.fetchStudents();
        this.fetchStages();
        this.fetchIntegrations();
    }

    fetchStudents = () => {
        fetch(backendUrl(`/${this.props.match.params.projectId}/group/${this.props.match.params.groupId}`), {
            method: "GET",
            credentials: "include",
        })
            .then(response => response.json())
            .then(json => this.setState({
                students: json
            }))
    };

    fetchStages = () => {
        fetch(backendUrl(`/preview/${this.props.match.params.groupId}/${this.props.match.params.projectId}/stages`), {
            method: "GET",
            credentials: "include",
            headers: {'Authorization': getAuthHeader()}
        })
            .then(response => response.json())
            .then(json => this.setState({
                stages: json
            }))
    };

    fetchIntegrations = () => {
        fetch(backendUrl(`/preview/${this.props.match.params.groupId}/${this.props.match.params.projectId}/integrations`), {
            method: "GET",
            credentials: "include",
            headers: {'Authorization': getAuthHeader()}
        })
            .then(response => response.json())
            .then(json => this.setState({
                integrations: json
            }))
    };

    stageChangedHandler = () => {
        this.fetchStages();
        this.fetchIntegrations();
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

    getStudentsNames = () => {
        var studentsNames = "";
        var i = 0;
        this.state.students.map(student => {
                studentsNames = studentsNames + student;
                if (i < this.state.students.length - 1) {
                    studentsNames = studentsNames + ", "
                }
                i++;
            }
        );

        return studentsNames
    };

    render() {
        return (
            <div className={this.props.classes.app}>
                <DescriptionAndSettingsItemComponent header={"Projekt: " + this.props.match.params.projectId}
                                                     descriptionInfo="Pobierz opis projektu"
                                                     getDescriptionActionHandler={this.handleDownloadProjectDescription}
                                                     settingsInfo="Pobierz konfigurację środowiska"
                                                     getSettingsActionHandler={this.handleDownloadProjectEnvironment}/>
                <div>
                    Grupa: {this.props.match.params.groupId} ({"first_user@mail.com"})
                </div>
                <Table className={this.props.classes.table}>
                    <TableHead>
                        <TableRow>
                            <CustomHeaderCell>Etapy</CustomHeaderCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.stages.stages.map(stage => (
                            <TableRow key={stage.stageName}>
                                <CustomTableCell component="th" scope="row">
                                    <ExpansionPanel>
                                        <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                                            <PreviewStudentStageRow
                                                stage={stage}
                                                groupName={this.props.match.params.groupId}
                                                projectName={this.props.match.params.projectId}
                                                stageChangedHandler={this.stageChangedHandler}/>
                                        </ExpansionPanelSummary>
                                        <ExpansionPanelDetails>
                                            <Typography>
                                                <div className={this.props.classes.panel}>
                                                    <div className={this.props.classes.testLabel}>
                                                    <p> Testy </p>
                                                    </div>
                                                    <PreviewStudentTestCasesDetails
                                                        testCases={stage.testCases}
                                                        groupName={this.props.match.params.groupId}
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
                            <CustomHeaderCell>Integracje</CustomHeaderCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.integrations.integrations.map(integration => (
                            <TableRow key={integration.integrationName}>
                                <CustomTableCell component="th" scope="row">
                                    <ExpansionPanel>
                                        <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                                            <PreviewStudentIntegrationRow
                                                integration={integration}
                                                groupName={this.props.match.params.groupId}
                                                projectName={this.props.match.params.projectId}
                                                integrationChangedHandler={this.integrationChangedHandler}/>
                                        </ExpansionPanelSummary>
                                        <ExpansionPanelDetails>
                                            <Typography>
                                                <div className={this.props.classes.panel}>
                                                    <div className={this.props.classes.testLabel}>
                                                    <p> Testy </p>
                                                    </div>
                                                    <PreviewStudentIntegrationTestCasesDetails
                                                        testCases={integration.testCases}
                                                        groupName={this.props.match.params.groupId}
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

export default withStyles(styles)(PreviewStudentProjectBoard);
