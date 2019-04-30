import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import backendUrl from "../backendUrl";
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Table from "@material-ui/core/Table/Table";
import {CustomTableCell, styles} from "../styles/ProjectBoardStyles";
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import StudentTestCasesDetails from "./StudentTestCasesDetails"
import StudentStageRow from "./StudentStageRow";

class StudentProjectBoard extends Component {

    constructor(props) {
        super(props);
        this.state = {stages: {stages: []}};
    }

    componentDidMount() {
        this.fetchStages();
    }

    fetchStages = () => {
        fetch(backendUrl(`/student/${this.props.match.params.projectId}/stages`), {
            method: "GET",
            credentials: "include"
        })
            .then(response => response.json())
            .then(json => this.setState({
                stages: json
            }))
    };

    stageChangedHandler = () => {
        this.fetchStages()
    };

    render() {
        return (
            <div className={this.props.classes.app}>
                <p>
                    Projekt: {this.props.match.params.projectId}
                </p>
                <Table className={this.props.classes.table}>
                    <TableHead>
                        <TableRow>
                            <CustomTableCell>Etapy</CustomTableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.stages.stages.map(stage => (
                            <TableRow key={stage.stageName}>
                                <CustomTableCell component="th" scope="row">
                                    <ExpansionPanel>
                                        <ExpansionPanelSummary expandIcon={<ExpandMoreIcon/>}>
                                            <StudentStageRow
                                                stage={stage}
                                                projectName={this.props.match.params.projectId}
                                                stageChangedHandler={this.stageChangedHandler}
                                            />
                                        </ExpansionPanelSummary>
                                        <ExpansionPanelDetails>
                                            <Typography>
                                                <div className={this.props.classes.panel}>
                                                    <StudentTestCasesDetails
                                                        testCases={stage.testCases}
                                                        projectName={this.props.match.params.projectId}
                                                        stageName={stage.stageName}
                                                    />
                                                </div>
                                            </Typography>
                                        </ExpansionPanelDetails>
                                    </ExpansionPanel>
                                </CustomTableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>
        );
    }
}

export default withStyles(styles)(StudentProjectBoard);
