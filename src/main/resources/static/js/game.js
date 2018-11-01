class Hint extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    if (this.props.direction != null) {
      return (
        <span
          className="hint"
          onMouseEnter={() => this.props.onMouseEnter(this.props.direction)}
          onMouseLeave={() => this.props.onMouseLeave(this.props.direction)}
        />
      )
    } else {
      return (
        <span
          className="hint"
        />
      )
    }
  }
}

class Square extends React.Component {
  constructor(props) {
    super(props);
    this.handleMouseEnter = this.handleMouseEnter.bind(this);
    this.handleMouseLeave = this.handleMouseLeave.bind(this);
  }

  handleMouseEnter(direction) {
    //console.log('handleMouseEnter ' + this.props.row + ',' + this.props.column + ' ' + direction)
    this.props.onShowHint(this.props.row, this.props.column, direction)
  }

  handleMouseLeave(direction) {
    //console.log('handleMouseLeave ' + this.props.row + ',' + this.props.column + ' ' + direction)
    this.props.onHideHint(this.props.row, this.props.column, direction)
  }

  render() {
    if (this.props.type == "empty") {
      let hints = [];
      hints.push(<Hint key="northwest"/>);

      if (this.props.northhint) {
        hints.push(<Hint
          key="north"
          direction="north"
          onMouseEnter={this.handleMouseEnter}
          onMouseLeave={this.handleMouseLeave}
        />);
      } else {
        hints.push(<Hint
          key="north"
        />);
      }

      hints.push(<Hint key="northeast"/>);

      if (this.props.westhint) {
        hints.push(<Hint
          key="west"
          direction="west"
          onMouseEnter={this.handleMouseEnter}
          onMouseLeave={this.handleMouseLeave}
        />);
      } else {
        hints.push(<Hint
          key="west"
        />);
      }

      hints.push(<Hint key="center"/>);

      if (this.props.easthint) {
        hints.push(<Hint
          key="east"
          direction="east"
          onMouseEnter={this.handleMouseEnter}
          onMouseLeave={this.handleMouseLeave}
        />);
      } else {
        hints.push(<Hint
          key="east"
        />);
      }

      hints.push(<Hint key="southwest"/>);

      if (this.props.southhint) {
        hints.push(<Hint
          key="south"
          direction="south"
          onMouseEnter={this.handleMouseEnter}
          onMouseLeave={this.handleMouseLeave}
        />);
      } else {
        hints.push(<Hint
          key="south"
        />);
      }

      hints.push(<Hint key="southeast"/>);

      return (
        <div className="emptysquare">
           {hints}
        </div>
      )
      //}
    } else {
      let img;
      if (this.props.type == "landscape") {
        img = "images/" + this.props.landscape + "-" + this.props.crowns + ".jpg";
      } else {
        img = "images/castle-" + this.props.color + ".jpg";
      }

      return (
        <img className="imagesquare" src={img}/>
      );
    }
  }
}

class Kingdom extends React.Component {
  constructor(props) {
    super(props);
    this.handleShowHint = this.handleShowHint.bind(this);
    this.handleHideHint = this.handleHideHint.bind(this);
  }

  handleShowHint(row0, column0, direction) {
    const square0 = this.props.placingTile.squares[0];
    const square1 = this.props.placingTile.squares[1];

    let row1 = row0;
    let column1 = column0;
    if (direction == "north") {
      row1 = row0-1;
    } else if (direction == "west") {
      column1 = column0-1;
    } else if (direction == "east") {
      column1 = column0+1;
    } else {
      row1 = row0+1;
    }
    console.log('handleShowHint (' + row0 + ',' + column0 + ') => ' + square0.landscape + ', (' + row1 + ',' + column1 + ') => ' + square1.landscape);
  }

  handleHideHint(row0, column0, direction) {
    console.log('handleHideHint ' + row0 + ',' + column0 + ' ' + direction)
  }

  render() {
    let row;
    let column;
    let squares = [];
    for (row = 0; row < this.props.squares.length; row++) {
      for (column = 0; column < this.props.squares[row].length; column++) {
        let component;
        const square = this.props.squares[row][column];
        if (square == null) {
          if (this.props.placingTile != null) {
            const northhint = row > 0 && this.props.squares[row-1][column] == null;
            const westhint = column > 0 && this.props.squares[row][column-1] == null;
            const easthint = column+1 < this.props.squares.length && this.props.squares[row][column+1] == null;
            const southhint = row+1 < this.props.squares.length && this.props.squares[row+1][column] == null;
            component =
              <Square
                key={row + ',' + column}
                type="empty"
                row={row}
                column={column}
                placingTile={this.props.placingTile}
                northhint={northhint}
                westhint={westhint}
                easthint={easthint}
                southhint={southhint}
                onShowHint={this.handleShowHint}
                onHideHint={this.handleHideHint}
              />;
          } else {
            component =
              <Square
                key={row + ',' + column}
                type="empty"
                row={row}
                column={column}
              />;
          }
        } else {
          if (square.landscape != null) {
            component =
              <Square
                key={row + ',' + column}
                type="landscape"
                landscape={square.landscape}
                crowns={squares.crowns}
              />;
          } else {
            component =
              <Square
                key={row + ',' + column}
                type="castle"
                color={this.props.playerColor}
              />;
          }
        }
        squares.push(component);
      }
    }
    let label = this.props.playerName;
    if (this.props.placingTile) {
      label += ", should place the tile";
    }

    // TODO: add shiftUp, shiftLeft, shiftDown, shiftRight
    return (
      <div className="kingdom">
        <div>{label}</div>
        <div className="squares-container">
          {squares}
        </div>
      </div>
    );
  }
}

class Tile extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    let ownerContent = "";
    if (this.props.tileOwner == null) {
      if (this.props.currentPlayer) {
        ownerContent =
          <button onClick={() => this.props.onTileSelection(this.props.currentPlayer, this.props.rank)}>
            {this.props.currentPlayer.name}
          </button>;
      } else {
        ownerContent = <span>unclaimed</span>;
      }
    }
    else {
      ownerContent = <span>{this.props.tileOwner.name}</span>;
    }

    return (
      <div className="tile">
        <Square
          type="landscape"
          landscape={this.props.squares[0].landscape}
          crowns={this.props.squares[0].crowns}
        />
        <Square
          type="landscape"
          landscape={this.props.squares[1].landscape}
          crowns={this.props.squares[1].crowns}
        />
        <div>{ownerContent}</div>
      </div>
    );
  }
}

class RoundTiles extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    let label = this.props.label;
    if (this.props.currentPlayer) {
      label += ", " + this.props.currentPlayer.name + " should select a tile"
    }

    const tiles = this.props.tiles.map((tile) =>
      <Tile
        key={tile.rank}
        rank={tile.rank}
        squares={tile.squares}
        tileOwner={tile.owner}
        currentPlayer={this.props.currentPlayer}
        onTileSelection={this.props.onTileSelection}
      />
    );

    return (
      <div className="stage">
        <div>{label}</div>
        <div className="tiles-container">
          {tiles}
        </div>
      </div>
    );
  }
}

class Game extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      kingdoms: []
    };
    this.handleTileSelection = this.handleTileSelection.bind(this);
    this.handleTilePlacement = this.handleTilePlacement.bind(this);
  }

  refreshGame(result)
  {
    this.setState({
      kingdoms: result.kingdoms,
      thisRoundTiles: result.thisRoundTiles,
      nextRoundTiles: result.nextRoundTiles,
      currentTurn: result.currentTurn
    });
  }

  componentDidMount() {
    fetch("getgame")
      .then(res => res.json())
      .then(
        (result) => {
          this.refreshGame(result);
        },
        // Note: it's important to handle errors here
        // instead of a catch() block so that we don't swallow
        // exceptions from actual bugs in components.
        (error) => {
          this.setState({
            error
          });
        }
      );
  }

  handleTileSelection(currentPlayer, rank) {
    fetch("claimtile/" + encodeURIComponent(currentPlayer.name) + "/" + rank, {
      method: 'PUT'
    })
    .then(res => res.json())
    .then(
      (result) => {
        this.refreshGame(result);
      },
      // Note: it's important to handle errors here
      // instead of a catch() block so that we don't swallow
      // exceptions from actual bugs in components.
      (error) => {
        this.setState({
          error
        });
      }
    );
  }

  handleTilePlacement(row0, col0, row1, col1) {
  }

  render() {
    const kingdoms = this.state.kingdoms;
    const currentTurn = this.state.currentTurn;

    let status;
    let thisRoundSelector = null;
    let nextRoundSelector = null;
    let placingTile = null;
    if (currentTurn != null) {
      if (currentTurn.task == "GAME_OVER") {
        status = "Winner: " + currentTurn.player.name;
      } else {
        status = "Next player: " + currentTurn.player.name + " to " + currentTurn.task;
        if (currentTurn.task == "CHOOSING_INITIAL_TILE") {
          thisRoundSelector = currentTurn.player;
        } else if (currentTurn.task == "CHOOSING_NEXT_TILE") {
          nextRoundSelector = currentTurn.player;
        } else if (currentTurn.task == "PLACING_TILE") {
          placingTile = this.state.thisRoundTiles[0];
        }
      }
    } else {
      status = "Initializing...";
    }

    const kingdomContent = this.state.kingdoms.map((kingdom) =>
      <Kingdom
        key={kingdom.player.name}
        playerName={kingdom.player.name}
        playerColor={kingdom.player.colorName}
        placingTile={currentTurn != null && currentTurn.player.name == kingdom.player.name
          ? placingTile : null}
        squares={kingdom.allSquares}
        onTilePlacement={this.handleTilePlacement}
      />
    );

    let thisRoundContent = "";
    if (this.state.thisRoundTiles != null) {
      thisRoundContent = (
        <RoundTiles
          label="This round"
          tiles={this.state.thisRoundTiles}
          currentPlayer={thisRoundSelector}
          onTileSelection={this.handleTileSelection}
        />
      );
    }
    let nextRoundContent = "";
    if (this.state.nextRoundTiles != null) {
      nextRoundContent = (
        <RoundTiles
          label="Next round"
          tiles={this.state.nextRoundTiles}
          currentPlayer={nextRoundSelector}
          onTileSelection={this.handleTileSelection}
        />
      );
    }

    return (
      <div>
        <div>
          {kingdomContent}
        </div>
        {thisRoundContent}
        {nextRoundContent}
        <div>
          {status}
        </div>
      </div>
    );
  }
}

ReactDOM.render(<Game />, document.getElementById("game"));