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
import AddNewProjectDialog from "../AddNewProjectDialog";

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

    render() {
        return (
            <div className={this.props.classes.app}>
                <ProjectHeader
                    display="block"
                    projectName={this.props.match.params.projectId}/>
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
                                        projectName={this.props.match.params.projectId}/>
                                </CustomTableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
                <AddNewProjectDialog
                    isOpen={this.state.isNewStageDialogVisible}
                    closeActionHandler={this.handleCloseNewStageDialog}
                    successActionHandler={this.fetchProjects}/>
            </div>
        );
    }
}

export default withStyles(styles)(ProjectBoard);
