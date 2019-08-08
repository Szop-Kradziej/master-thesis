import React from "react";
import {withStyles} from "@material-ui/core";
import TableRow from "@material-ui/core/TableRow/TableRow";
import {Link} from "react-router-dom";
import {CustomTableCell, styles} from "../../styles/ProjectssBoardStyles";

class PreviewGroupRow extends React.Component {

    render() {
        return (
            <TableRow key={this.props.group.groupName}>
                <CustomTableCell>
                    <Link to={`/preview/student/${this.props.projectName}/${this.props.group.groupName}`} className={this.props.classes.link}>
                        {this.props.group.groupName}
                    </Link>
                </CustomTableCell>
                <CustomTableCell>
                    {this.props.group.students.map(student => (
                        <div>{student}</div>
                    ))}
                </CustomTableCell>
            </TableRow>
        );
    }
}

export default withStyles(styles)(PreviewGroupRow)