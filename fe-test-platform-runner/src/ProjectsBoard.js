import React from "react";
import backendUrl from "./backendUrl";
import {withStyles} from "@material-ui/core";
import Table from "@material-ui/core/Table/Table";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import TableCell from "@material-ui/core/TableCell/TableCell";

class ProjectsBoard extends React.Component {

    constructor(props) {
        super(props);
        this.state = {projects: {projects: []}};
    }

    componentDidMount() {
        this.fetchScores();
    }

    fetchScores = () => {
        fetch(backendUrl(`/projects`), {
            method: "GET",
            credentials: "include"
        })
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
                            <CustomTableCell>Nazwa projektu</CustomTableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.projects.projects.map(project => (
                            <TableRow key={project.id}>
                                <CustomTableCell component="th" scope="row">
                                    {project}
                                </CustomTableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>

            </div>
        );
    }
}

const CustomTableCell = withStyles(() => ({
    head: {
        color: "black",
        fontWeight: 700,
        fontSize: 16
    },
    body: {
        color: "black",
        fontSize: 16
    }
}))(TableCell);

const styles = () => ({
    app: {
        textAlign: "center",
        backgroundColor: "#e0e0e0",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        fontSize: "calc(10px + 2vmin)",
        color: "black"
    },
    table: {
        minWidth: 900,
        fontSize: 16,
        color: "black"
    }
});

export default withStyles(styles)(ProjectsBoard)