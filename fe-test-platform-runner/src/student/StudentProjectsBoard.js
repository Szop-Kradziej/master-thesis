import React from "react";
import {withStyles} from "@material-ui/core";
import Table from "@material-ui/core/Table/Table";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import {Link} from "react-router-dom";
import {CustomTableCell, styles} from "../styles/ProjectssBoardStyles";
import * as Api from "../Api";

class StudentProjectsBoard extends React.Component {

    constructor(props) {
        super(props);
        this.state = {projects: {projects: []}};
    }

    componentDidMount() {
        this.fetchProjects();
    }

    fetchProjects = () => {
        //TODO: Add request to call for projects for specific user
        Api.fetchStudentProjects()
            .then(response => response.json())
            .then(json => this.setState({
                projects: json
            }))
    };

    render() {
        return (
            <div>
                <Table className={this.props.classes.table}>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell>Projekty</CustomTableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.projects.projects.map(project => (
                            <TableRow key={project}>
                                <CustomTableCell component="th" scope="row">
                                    <Link to={`/student/projects/${project}`} className={this.props.classes.link}>
                                        {project}
                                    </Link>
                                </CustomTableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>
        );
    }
}

export default withStyles(styles)(StudentProjectsBoard)