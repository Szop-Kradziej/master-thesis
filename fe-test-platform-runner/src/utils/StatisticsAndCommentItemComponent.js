import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import StatisticsIcon from "@material-ui/icons/Assessment";
import InputWrapper from "./InputWrapper";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";
import CommentIcon from "@material-ui/icons/Info";

class StatisticsAndCommentItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.statisticsInfo}
                            disabled={this.props.statisticsDisabled}
                            onClick={this.props.getStatisticsActionHandler}>
                    <Tooltip title={this.props.statisticsInfo}>
                        <StatisticsIcon/>
                    </Tooltip>
                </IconButton>
                <IconButton disabled={this.props.commentDisabled}>
                    <Tooltip title={this.props.commentInfo}>
                        <CommentIcon/>
                    </Tooltip>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default StatisticsAndCommentItemComponent;
