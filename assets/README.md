# Assets Directory

This directory contains visual assets for the Smart Expense Tracker project.

## Files
- `architecture.png` - System architecture diagram
- `architecture.svg` - Vector version of architecture diagram (optional)

## How to Generate Architecture Diagram

### Method 1: Using Mermaid Live Editor (Recommended)
1. Go to https://mermaid.live/
2. Copy the Mermaid code from README.md
3. Click "Export" → "PNG" 
4. Save as `architecture.png` in this directory

### Method 2: Using Mermaid CLI
```bash
npm install -g @mermaid-js/mermaid-cli
mmdc -i architecture.mmd -o architecture.png -w 1200 -H 800
```

### Method 3: Using VS Code Extension
1. Install "Mermaid Markdown Syntax Highlighting" extension
2. Open a .md file with the Mermaid diagram
3. Right-click on diagram → "Export Mermaid Diagram"
```