import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import {styles} from "../../../../styles/ProjectBoardStyles";
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import IntegrationHeader from "./IntegrationHeader";

class IntegrationComponent extends Component {

    render() {
        return (
            <ExpansionPanel>
                <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                    <Typography className={this.props.classes.heading}>
                        <IntegrationHeader
                            projectName={this.props.projectName}
                            integration={this.props.integration}
                            integrationChangedHandler={this.props.integrationChangedHandler}/>
                    </Typography>
                </ExpansionPanelSummary>
                <ExpansionPanelDetails>
                    <Typography className={this.props.classes.heading}>
                        <div className={this.props.classes.panel}>
                            TODO
                        </div>
                    </Typography>
                </ExpansionPanelDetails>
            </ExpansionPanel>
        );
    }
}

export default withStyles(styles)(IntegrationComponent);
