import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import {CustomTableCell, styles} from "../../../styles/ProjectBoardStyles";
import AddNewItemComponent from "../../../utils/AddNewItemComponent";
import * as Api from "../../../Api";
import ProjectHeader from "./ProjectHeader";
import StageComponent from "./stage/StageComponent";
import AddNewStageDialog from "./AddNewStageDialog";
import GroupComponent from "./group/GroupComponent";

class ProjectBoard extends Component {

    constructor(props) {
        super(props);
        this.state = {stages: {stages: []}, groups: {groups: []}, isNewStageDialogVisible: false};
    }

    componentDidMount() {
        this.fetchStages();
        this.fetchGroups();
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

    handleOpenNewStageDialog = () => {
        this.setState({isNewStageDialogVisible: true});
    };

    handleCloseNewStageDialog = () => {
        this.setState({isNewStageDialogVisible: false});
    };

    render() {
        return (
            <div className={this.props.classes.app}>
                <ProjectHeader
                    display="block"
                    projectName={this.props.match.params.projectId}
                    projectDescription={this.state.stages.projectDescription}
                    projectChangedHandler={this.fetchStages}/>
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
                                    <StageComponent
                                        stage={stage}
                                        projectName={this.props.match.params.projectId}
                                        stageChangedHandler={this.fetchStages}/>
                                </CustomTableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
                <Table className={this.props.classes.table}>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell>
                                <div display="inline">
                                    <AddNewItemComponent
                                        header="Grupy"
                                        info="Dodaj nową grupę"
                                        addActionHandler={this.handleOpenNewStageDialog}/>
                                    <AddNewItemComponent
                                        header="Grupy (JSON)"
                                        info="Dodaj nową grupę"
                                        addActionHandler={this.handleOpenNewStageDialog}/>
                                </div>
                            </CustomTableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
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
                    </TableBody>
                </Table>
                <AddNewStageDialog
                    isOpen={this.state.isNewStageDialogVisible}
                    projectName={this.props.match.params.projectId}
                    closeActionHandler={this.handleCloseNewStageDialog}
                    successActionHandler={this.fetchStages}/>
            </div>
        );
    }
}

export default withStyles(styles)(ProjectBoard);
