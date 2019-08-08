import React from "react";
import {withStyles} from "@material-ui/core";
import TableRow from "@material-ui/core/TableRow/TableRow";
import {Link} from "react-router-dom";
import {CustomTableCell, styles} from "../../styles/ProjectssBoardStyles";

class PreviewProjectRow extends React.Component {

    render() {
        return (
            <TableRow key={this.props.projectName}>
                <CustomTableCell width="95%" component="th" scope="row">
                    <Link to={`/preview/${this.props.projectName}`} className={this.props.classes.link}>
                        {this.props.projectName}
                    </Link>
                </CustomTableCell>
            </TableRow>
        );
    }
}

export default withStyles(styles)(PreviewProjectRow)