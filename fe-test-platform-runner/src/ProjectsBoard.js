import React from "react";
import backendUrl from "./backendUrl";
import {withStyles} from "@material-ui/core";
import Table from "@material-ui/core/Table/Table";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Button from "@material-ui/core/Button/Button";
import Modal from "@material-ui/core/Modal/Modal";
import TextField from "@material-ui/core/TextField/TextField";
import {Link} from "react-router-dom";
import {styles, CustomTableCell, getModalStyle} from "./styles/ProjectssBoardStyles";

class ProjectsBoard extends React.Component {

    constructor(props) {
        super(props);
        this.state = {projects: {projects: []}, isNewProjectDialogVisible: false, newProjectName: null};
    }

    componentDidMount() {
        this.fetchProjects();
    }

    fetchProjects = () => {
        fetch(backendUrl(`/projects`), {
            method: "GET",
            credentials: "include"
        })
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

    handleNewProjectNameAdded = () => event => {
        this.setState({newProjectName: event.target.value})
    };

    handleAddNewProject = () => {
        fetch(backendUrl(`/project`), {
            method: "POST",
            credentials: "include",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: "projectName=" + this.state.newProjectName
        })
            .then(() => this.setState({isNewProjectDialogVisible: false, newProjectName: null}))
            .then(this.fetchProjects)
    };

    render() {
        return (
            <div>
                <Table className={this.props.classes.table}>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell>Nazwa projektu</CustomTableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.projects.projects.map(project => (
                            <TableRow key={project}>
                                <CustomTableCell component="th" scope="row">
                                    <Link to={`/projects/${project}`} className={this.props.classes.link}>
                                        {project}
                                    </Link>
                                </CustomTableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
                <Button className={this.props.classes.button} onClick={this.handleOpenNewProjectDialog}>
                    Dodaj nowy projekt
                </Button>

                <Modal open={this.state.isNewProjectDialogVisible}>
                    <div style={getModalStyle()} className={this.props.classes.paper}>
                        <form className={this.props.classes.container} noValidate autoComplete="off">
                            <TextField
                                id="standard-name"
                                label="Nazwa projektu"
                                className={this.props.classes.textField}
                                value={this.state.newProjectName}
                                onChange={this.handleNewProjectNameAdded()}
                                margin="normal"
                            />
                            <Button className={this.props.classes.button} onClick={this.handleAddNewProject}>
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

export default withStyles(styles)(ProjectsBoard)