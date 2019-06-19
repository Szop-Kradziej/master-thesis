import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import {styles} from "../../../../styles/ProjectBoardStyles";
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import TestCasesDetails from "./TestCasesDetails";
import StageHeader from "./StageHeader";

class StageComponent extends Component {

    render() {
        return (
            <ExpansionPanel>
                <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                    <Typography className={this.props.classes.heading}>
                        <StageHeader
                            projectName={this.props.projectName}
                            stage={this.props.stage}
                            stageChangedHandler={this.props.stageChangedHandler}/>
                    </Typography>
                </ExpansionPanelSummary>
                <ExpansionPanelDetails>
                    <Typography className={this.props.classes.heading}>
                        <div className={this.props.classes.panel}>
                            <TestCasesDetails
                                testCases={this.props.stage.testCases}
                                projectName={this.props.projectName}
                                stageName={this.props.stage.stageName}
                                stageChangedHandler={this.props.stageChangedHandler}/>
                        </div>
                    </Typography>
                </ExpansionPanelDetails>
            </ExpansionPanel>
        );
    }
}

export default withStyles(styles)(StageComponent);
