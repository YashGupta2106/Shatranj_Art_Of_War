import React, { useState, useEffect } from 'react';
import './EvalBar.css';

const EvalBar = ({ 
  evaluation = 0, // Evaluation in centipawns (positive = white advantage)
  isMate = false, 
  mateIn = 0,
  height = 400 
}) => {
  const [displayEval, setDisplayEval] = useState(0);
  const [isDramatic, setIsDramatic] = useState(false);

  // Smooth evaluation transition
  useEffect(() => {
    const evalDiff = Math.abs(evaluation - displayEval);
    
    // Trigger dramatic animation for large eval changes
    if (evalDiff > 200) {
      setIsDramatic(true);
      setTimeout(() => setIsDramatic(false), 600);
    }
    
    // Smooth transition
    const timer = setTimeout(() => {
      setDisplayEval(evaluation);
    }, 100);
    
    return () => clearTimeout(timer);
  }, [evaluation, displayEval]);

  // Convert centipawns to percentage for bar height
  const getBarPercentage = (evaluation) => {
    if (isMate) {
      return mateIn > 0 ? 95 : 5; // White mate vs Black mate
    }
    
    // Clamp evaluation between -1000 and +1000 centipawns
    const clampedEval = Math.max(-1000, Math.min(1000, eval));
    
    // Convert to percentage (50% = equal, 0% = black winning, 100% = white winning)
    return 50 + (clampedEval / 1000) * 45;
  };

  // Format evaluation for display
  const formatEvaluation = (evaluation) => {
    if (isMate) {
      return `M${Math.abs(mateIn)}`;
    }
    
    const pawns = Math.abs(eval) / 100;
    const sign = eval >= 0 ? '+' : '-';
    
    if (pawns >= 10) {
      return `${sign}${Math.round(pawns)}`;
    }
    
    return `${sign}${pawns.toFixed(1)}`;
  };

  // Get advantage text
  const getAdvantageText = () => {
    if (isMate) {
      return mateIn > 0 ? 'White Mates' : 'Black Mates';
    }
    
    if (Math.abs(displayEval) < 25) return 'Equal';
    if (displayEval > 0) return 'White';
    return 'Black';
  };

  // Get advantage class
  const getAdvantageClass = () => {
    if (Math.abs(displayEval) < 25) return 'advantage-equal';
    return displayEval > 0 ? 'advantage-white' : 'advantage-black';
  };

  const whitePercentage = getBarPercentage(displayEval);
  const blackPercentage = 100 - whitePercentage;

  return (
    <div className="eval-bar-wrapper">
      {/* Evaluation Labels */}
      <div className="eval-labels">
        <div className="eval-label-top">+∞</div>
        <div className="eval-label-center">0</div>
        <div className="eval-label-bottom">-∞</div>
      </div>

      {/* Main Evaluation Bar */}
      <div 
        className={`eval-bar-container ${isDramatic ? 'eval-bar-dramatic' : ''}`}
        style={{ height: `${height}px` }}
      >
        {/* Black advantage section (top) */}
        <div 
          className="eval-bar-black"
          style={{ height: `${blackPercentage}%` }}
        />
        
        {/* White advantage section (bottom) */}
        <div 
          className="eval-bar-white"
          style={{ height: `${whitePercentage}%` }}
        />
        
        {/* Center line */}
        <div className="eval-bar-center" />
        
        {/* Mate indicator */}
        {isMate && (
          <div className="eval-mate">
            {formatEvaluation(displayEval)}
          </div>
        )}
        
        {/* Tooltip */}
        <div className="eval-tooltip">
          {formatEvaluation(displayEval)}
          {!isMate && (
            <div style={{ fontSize: '12px', opacity: 0.8 }}>
              {Math.abs(displayEval)} centipawns
            </div>
          )}
        </div>
      </div>

      {/* Advantage Indicator */}
      <div className={`advantage-indicator ${getAdvantageClass()}`}>
        {getAdvantageText()}
      </div>
    </div>
  );
};

export default EvalBar;
