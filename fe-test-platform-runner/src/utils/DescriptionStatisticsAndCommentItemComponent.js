import React, {Component} from "react";
import IconButton from "@material-ui/core/IconButton/IconButton";
import DescriptionIcon from "@material-ui/icons/Description";
import StatisticsIcon from "@material-ui/icons/Assessment";
import CommentIcon from "@material-ui/icons/Info";
import InputWrapper from "./InputWrapper";
import Tooltip from "@material-ui/core/Tooltip/Tooltip";

class DescriptionStatisticsAndCommentItemComponent extends Component {

    render() {
        return (
            <InputWrapper>
                {this.props.header}
                <IconButton aria-label={this.props.descriptionInfo}
                            onClick={this.props.getDescriptionActionHandler}>
                    <Tooltip title={this.props.descriptionInfo}>
                        <DescriptionIcon/>
                    </Tooltip>
                </IconButton>
                <IconButton aria-label={this.props.statisticsInfo}
                            disabled={this.props.statisticsDisabled}
                            onClick={this.props.getStatisticsActionHandler}>
                    <Tooltip title={this.props.statisticsInfo}>
                        <StatisticsIcon/>
                    </Tooltip>
                </IconButton>
                <IconButton>
                    <Tooltip title={this.props.commentInfo}>
                        <CommentIcon/>
                    </Tooltip>
                </IconButton>
            </InputWrapper>
        );
    }
}

export default DescriptionStatisticsAndCommentItemComponent;
