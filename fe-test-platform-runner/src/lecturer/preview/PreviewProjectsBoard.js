import React from "react";
import {withStyles} from "@material-ui/core";
import Table from "@material-ui/core/Table/Table";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import {CustomTableCell, styles} from "../../styles/ProjectssBoardStyles";
import * as Api from "../../Api";
import PreviewProjectRow from "./PreviewProjectRow";

class PreviewProjectsBoard extends React.Component {

    constructor(props) {
        super(props);
        this.state = {projects: {projects: []}};
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

    render() {
        return (
            <div>
                <Table className={this.props.classes.table}>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell>
                                Projekty
                            </CustomTableCell>
                            <CustomTableCell/>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.projects.projects.map(project => (
                            <PreviewProjectRow projectName={project}/>
                        ))}
                    </TableBody>
                </Table>
            </div>
        );
    }
}

export default withStyles(styles)(PreviewProjectsBoard)