import React from "react";
import {withStyles} from "@material-ui/core";
import Table from "@material-ui/core/Table/Table";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import {CustomTableCell, styles} from "../../../styles/ProjectssBoardStyles";
import * as Api from "../../../Api";
import TableBody from "@material-ui/core/TableBody/TableBody";
import PreviewGroupRow from "./PreviewGroupRow";

class PreviewBoard extends React.Component {

    constructor(props) {
        super(props);
        this.state = {groups: {groups: []}};
    }

    componentDidMount() {
        this.fetchGroups();
    }

    fetchGroups = () => {
        Api.fetchGroups(this.props.match.params.projectId)
            .then(response => response.json())
            .then(json => this.setState({
                groups: json
            }))
    };

    render() {
        return (
            <div>
                <Table className={this.props.classes.table}>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell>
                                Grupy
                            </CustomTableCell>
                            <CustomTableCell>
                                Studenci
                            </CustomTableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.groups.groups.map(group => (
                            <PreviewGroupRow
                                projectName={this.props.match.params.projectId}
                                group={group}/>
                        ))}
                    </TableBody>
                </Table>
            </div>
        );
    }
}

export default withStyles(styles)(PreviewBoard)