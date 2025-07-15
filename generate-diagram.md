# Generate Architecture Diagram

## Quick Steps to Create architecture.png

### Option 1: Mermaid Live Editor (Easiest)
1. Go to https://mermaid.live/
2. Copy the content from `assets/architecture.mmd`
3. Paste it into the editor
4. Click "Actions" → "Export PNG"
5. Save as `assets/architecture.png`

### Option 2: Using Mermaid CLI
```bash
# Install Mermaid CLI
npm install -g @mermaid-js/mermaid-cli

# Generate PNG from the .mmd file
mmdc -i assets/architecture.mmd -o assets/architecture.png -w 1200 -H 800 --backgroundColor white
```

### Option 3: VS Code with Mermaid Extension
1. Install "Mermaid Markdown Syntax Highlighting" extension
2. Open `assets/architecture.mmd`
3. Right-click → "Export Mermaid Diagram" → PNG

### Option 4: Online Mermaid Editors
- https://mermaid.live/
- https://mermaid-js.github.io/mermaid-live-editor/
- https://kroki.io/

## Expected Output
- File: `assets/architecture.png`
- Size: ~1200x800 pixels
- Format: PNG with transparent or white background
- Quality: High resolution for documentation

## After Creating the Image
1. Save as `assets/architecture.png`
2. The README.md will automatically reference this image
3. Commit and push to GitHub