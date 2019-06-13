import React from "react";
import {withStyles} from "@material-ui/core";
import Table from "@material-ui/core/Table/Table";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import {CustomTableCell, styles} from "../../styles/ProjectssBoardStyles";
import AddNewItemComponent from "../../utils/AddNewItemComponent";
import AddNewProjectDialog from "./AddNewProjectDialog";
import * as Api from "../../Api";
import ProjectRow from "./ProjectRow";

class ProjectsBoard extends React.Component {

    constructor(props) {
        super(props);
        this.state = {projects: {projects: []}, isNewProjectDialogVisible: false};
    }

    componentDidMount() {
        this.fetchProjects();
    }

    fetchProjects = () => {
        Api.fetchProjects()
            .then(response => response.json())
            .then(json => this.setState({
                projects: json
            }))
    };

    handleOpenNewProjectDialog = () => {
        this.setState({isNewProjectDialogVisible: true});
    };

    handleCloseNewProjectDialog = () => {
        this.setState({isNewProjectDialogVisible: false});
    };

    render() {
        return (
            <div>
                <Table className={this.props.classes.table}>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell>
                                <AddNewItemComponent
                                    header="Projekty"
                                    info="Dodaj nowy projekt"
                                    addActionHandler={this.handleOpenNewProjectDialog}/>
                            </CustomTableCell>
                            <CustomTableCell/>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.projects.projects.map(project => (
                            <ProjectRow
                                projectName={project}
                                projectChangedHandler={this.fetchProjects}/>
                        ))}
                    </TableBody>
                </Table>
                <AddNewProjectDialog
                    isOpen={this.state.isNewProjectDialogVisible}
                    closeActionHandler={this.handleCloseNewProjectDialog}
                    successActionHandler={this.fetchProjects}/>
            </div>
        );
    }
}

export default withStyles(styles)(ProjectsBoard)