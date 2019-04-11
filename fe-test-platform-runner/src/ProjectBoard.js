import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import backendUrl from "./backendUrl";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import TableCell from "@material-ui/core/TableCell/TableCell";


class ProjectBoard extends Component {

    constructor(props) {
        super(props);
        this.state = {stages: {stages: []}};
    }

    componentDidMount() {
        this.fetchStages();
    }

    fetchStages = () => {
        fetch(backendUrl(`/${this.props.match.params.projectId}/stages`), {
            method: "GET",
            credentials: "include"
        })
            .then(response => response.json())
            .then(json => this.setState({
                stages: json
            }))
    };

    render() {
        return (
            <div className={this.props.classes.app}>
                <p>
                    Nazwa projektu: {this.props.match.params.projectId}
                </p>
                <Table className={this.props.classes.table}>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell>Nazwa etapu</CustomTableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.stages.stages.map(stage => (
                            <TableRow key={stage}>
                                <CustomTableCell component="th" scope="row">
                                    {stage}
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
    },
    table: {
        minWidth: 900,
        fontSize: 16,
        color: "black"
    },
});

export default withStyles(styles)(ProjectBoard);
