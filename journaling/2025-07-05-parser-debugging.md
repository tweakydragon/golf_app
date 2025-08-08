# Journal Entry - Parser Debugging Session
**Date**: July 5, 2025  
**Session**: Debugging Awesome Golf CSV parser issues

## Problem Investigation

### Issue: Still Getting Null Values
Despite fixing the header parsing logic, the Awesome Golf CSV parser is still returning all null values for shot metrics. The upload succeeds and creates shots, but all data fields are empty.

### Debugging Attempts

#### 1. **Header Parsing Fix**
- **What I Changed**: Fixed the two-header row reading logic
- **Before**: Parser expected headers with bracket notation like `[mph]`, `[yd]`
- **After**: Parser now reads first row as headers, skips second row (units)
- **Result**: Still getting null values

#### 2. **Debug Logging Addition**
- **Added INFO level logs** to track parsing process
- **Controller logs**: Added to verify which parser branch is taken
- **Service logs**: Added to track header/value parsing
- **Problem**: Logs not appearing in output despite INFO level

#### 3. **Log Level Investigation**
- Spring Boot DEBUG logs are showing (Hibernate SQL, HTTP processing)
- But custom INFO logs from CsvService and SessionController not visible
- Indicates possible log configuration issue or log filtering

### Current Status

**✅ Upload Infrastructure**: Working correctly
- File upload succeeds
- Sessions created with proper metadata
- Shot entities inserted into database
- Timestamps parsed correctly

**❌ Data Parsing**: Not working  
- All shot metrics remain null
- Headers likely not matching switch cases
- Column mapping logic needs verification

## Next Debugging Steps

### 1. **Manual Column Inspection**
Instead of relying on logs, manually verify what headers are being read:
- Print the actual CSV structure
- Check exact header strings vs switch case strings
- Verify column indices

### 2. **Simple Test Case**
Create a minimal test to verify:
- Are headers being read correctly?
- Are case conversions working?
- Are the switch statements matching?

### 3. **Parser Logic Verification**
Review the core parsing logic:
- Column index mapping
- String trimming and case conversion
- parseDouble() method functionality

### 4. **Alternative Debugging**
- Use exception handling to catch and log parsing errors
- Add system output instead of logger
- Create unit test for the parser method

## Hypothesis

**Most Likely Cause**: The header strings from the CSV don't exactly match the switch case strings.

**Possible Issues**:
1. **Case sensitivity**: Headers might have different casing
2. **Whitespace**: Extra spaces or hidden characters
3. **Character encoding**: Special characters in headers
4. **Column order**: Parser might be reading wrong columns

## Action Plan

1. **Manual header verification** - Print exact header strings
2. **Case-insensitive debugging** - Check all string comparisons  
3. **Fallback parser** - Create simple index-based parser
4. **Unit test** - Isolate the parsing logic for testing

The fact that timestamps are being parsed correctly but data fields aren't suggests the issue is specifically in the header-to-field mapping logic in the switch statement.