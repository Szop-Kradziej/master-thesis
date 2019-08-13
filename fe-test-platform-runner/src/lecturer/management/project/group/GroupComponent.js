import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import {styles} from "../../../../styles/ProjectBoardStyles";
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import GroupHeader from "./GroupHeader";
import StudentsDetails from "./StudentsDetails";

class GroupComponent extends Component {

    render() {
        return (
            <ExpansionPanel>
                <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                    <Typography className={this.props.classes.heading}>
                        <GroupHeader
                            projectName={this.props.projectName}
                            groupName={this.props.group.groupName}
                            groupChangedHandler={this.props.groupChangedHandler}/>
                    </Typography>
                </ExpansionPanelSummary>
                <ExpansionPanelDetails>
                    <Typography className={this.props.classes.testsHeading}>
                        <div className={this.props.classes.panel}>
                            <StudentsDetails
                                groupName={this.props.group.groupName}
                                projectName={this.props.projectName}
                                students={this.props.group.students}
                                groupChangedHandler={this.props.groupChangedHandler}/>
                        </div>
                    </Typography>
                </ExpansionPanelDetails>
            </ExpansionPanel>
        );
    }
}

export default withStyles(styles)(GroupComponent);
